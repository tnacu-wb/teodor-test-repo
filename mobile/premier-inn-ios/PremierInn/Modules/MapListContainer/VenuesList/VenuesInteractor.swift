//
//  VenuesInteractor.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import MapKit
import SimpleNetwork

extension Hotel: @retroactive Venue {}

enum VenuesInteractorError: LocalizedError {
    case hotelFullyBooked
    case noAvailableHotel

    var errorDescription: String? {
        switch self {
        case .hotelFullyBooked:
            return PILocalizedString(
            	"searchResultsFullyBookedFallbackMessage",
            	comment: "Search results: hotel fully booked message fallback"
            )
        case .noAvailableHotel:
            return PILocalizedString("mapNoSearchResultsError", comment: "")
        }
    }
}

protocol VenueInteractorProtocol {
	var venues: [Venue] { get }
    var criteria: Criteria { get }
    var suggestion: Suggestion? { get }
    var lastSuccessfulSuggestion: Suggestion? { get }
	var screenTitle: String? { get }
	var screenSubtitle: String? { get }
	var suggestionAndAllVenues: [Venue] { get }
	var suggestionAndVenuesInReasonableDistance: [Venue] { get }
    var error: LocalizedError? { get }
    var shouldShowCoronavirusInformationBanner: Bool { get }

	func fetchHotels(with sorting: AvailabilitiesSorting, completion: @escaping (Bool) -> Void)
    func fetchHotelsNearby(with sorting: AvailabilitiesSorting, completion: @escaping (Bool) -> Void)
    func fetchHotels(
    	hotelDetailsRouterDelegate: HotelDetailsRouterDelegate?,
    	completion: @escaping (_ success: Bool, _ nextScreen: UIViewController?) -> Void
    )
	func trackAvailability(style: VenuesListStyle)
	func logToFirebase()
    func indexOfHotel(with code: String) -> Int?
    func shouldShowFallBack() -> Bool

    func updateSuggestion(to suggestion: Suggestion)
    func didTapDismissCoronavirusInformationBanner()
}

class VenuesInteractor {
    private var hotels: [Hotel]?
	private var aSuggestion: Suggestion?
    private var successfulSuggestion: Suggestion?
	private let requestsManager = RequestsManager()
	private var unavailableHotelCode: String?
	private var currentSorting: AvailabilitiesSorting = .distance
    var analytics: AnalyticsType = AnalyticsManager.shared

    var error: LocalizedError?

	deinit {
		requestsManager.cancelConnections()
	}

	init(hotels: [Hotel]?, suggestion: Suggestion?, unavailableHotelCode: String?) {
        self.hotels = reordered(hotels: hotels, forPriorityHotelWith: unavailableHotelCode)
		self.aSuggestion = suggestion
		self.unavailableHotelCode = unavailableHotelCode
        self.error = getError(unavailableHotelCode: unavailableHotelCode, hotels: hotels)
	}

    private func getError(
        unavailableHotelCode: String?,
        hotels: [Hotel]?
    ) -> LocalizedError? {
        if unavailableHotelCode != nil {
            return VenuesInteractorError.hotelFullyBooked
        }

        if let hotels = hotels, hotels.isEmpty {
            return VenuesInteractorError.noAvailableHotel
        }

        return nil
    }

    private var suggestionAndFewVenues: [Venue] {
        var results: [Venue] = []

        if let suggestion = suggestion {
            results.append(suggestion)
        }

        results.append(contentsOf: Array(venues.prefix(2)))

        return results
    }
}

extension VenuesInteractor: VenueInteractorProtocol {
    var criteria: Criteria { BookingDetails.sharedInstance.criteria }
	var screenTitle: String? { suggestion?.title }
	var screenSubtitle: String? { BookingDetails.sharedInstance.criteria.titleSummary }
    var suggestion: Suggestion? { aSuggestion }
    var lastSuccessfulSuggestion: Suggestion? { successfulSuggestion }
    var venues: [Venue] { hotels ?? [] }
    var shouldShowCoronavirusInformationBanner: Bool {
    	UserDefaults.standard.bool(forKey: Constants.dismissedCoronavirusMessaging) == false && SettingsManager.sharedInstance
    	.coronavirusMessagingSRPAndHDP }

    func updateSuggestion(to suggestion: Suggestion) {
        aSuggestion = suggestion
    }

	func fetchHotels(with sorting: AvailabilitiesSorting, completion: @escaping (Bool) -> Void) {
		currentSorting = sorting

        guard let suggestion = aSuggestion else {
            completion(false)
            return
        }

        handleAvailabilitiesCall(suggestion: suggestion, sorting: sorting, completion: completion)
	}

    func fetchHotelsNearby(with sorting: AvailabilitiesSorting, completion: @escaping (Bool) -> Void) {
        currentSorting = sorting

        let suggestion = PISuggestion(coordinate: Constants.Config.defaultSearchCoordinate)

        handleAvailabilitiesCall(suggestion: suggestion, sorting: sorting, completion: completion)
    }

    private func handleAvailabilitiesCall(
    	suggestion: Suggestion,
    	sorting: AvailabilitiesSorting,
    	completion: @escaping (Bool) -> Void
    ) {
        searchHotelAvailabilities(suggestion: suggestion, sorting: sorting, completion: completion)
    }

    private func searchHotelAvailabilities(
    	suggestion: Suggestion,
    	sorting: AvailabilitiesSorting,
    	completion: @escaping (Bool) -> Void
    ) {
        requestsManager.loadResultsWith(suggestion: suggestion, sorting: sorting) { availabilitiesResponse, _, error, _ in
            self.analytics.log(
            	event: FirebaseAnalytics.Event.graphQLAvailabilities,
            	parameters: ["success": availabilitiesResponse != nil]
            )
            self.handleAvailabilitiesResponse(response: availabilitiesResponse, error: error, completion: completion)
        }
    }

    private func handleAvailabilitiesResponse(
    	response: AvailabilitiesResponse?,
    	error: Error?,
    	completion: @escaping (Bool) -> Void
    ) {
        if let error = error, response == nil {
            analytics.track(error: error, name: PIAnalytics.Error.remoteAvailabilitiesLoadError)
            completion(false)
            return
        }

        self.hotels = self.reordered(hotels: response?.hotels, forPriorityHotelWith: self.unavailableHotelCode)

        self.error = getError(
            unavailableHotelCode: self.unavailableHotelCode,
            hotels: response?.hotels
        )

        completion(true)
    }

    func fetchHotels(
    	hotelDetailsRouterDelegate: HotelDetailsRouterDelegate?,
    	completion: @escaping (_ success: Bool, _ nextScreen: UIViewController?) -> Void
    ) {
        guard let suggestion = aSuggestion else {
            completion(false, nil)
            return
        }

        let searchSuggestion: Suggestion = {
            if UIDevice.current.userInterfaceIdiom == .pad {
                guard let name = suggestion.title else { return suggestion }
                return PISuggestion(dictionary: [
                	"name": name,
                	"lat": suggestion.coordinate.latitude,
                	"long": suggestion.coordinate.longitude
                ])
            }
            return suggestion
        }()

        requestsManager
        	.loadResultsWith(
        		suggestion: searchSuggestion,
        		sorting: currentSorting
        	) { manyAvailabilitiesResponse, singleAvailabilityResponse, error, unavailableHotelCode  in
            if let error = error, manyAvailabilitiesResponse == nil, singleAvailabilityResponse == nil {
                if let error = error as? MigrationServiceError, error == .fallbackToBePresented {
                    let alertController = AlertManager.fallbackToWebsitePopup()

                    return completion(true, alertController)
                }
                self.analytics.track(error: error, name: PIAnalytics.Error.remoteAvailabilitiesLoadError)

                return completion(false, nil)
            }

            if let response = singleAvailabilityResponse, let hotelCode = suggestion.identifier {
                let delegate: HotelDetailsRouterDelegate? = {
                    guard UIDevice.current.userInterfaceIdiom == .pad else { return nil }
                    return hotelDetailsRouterDelegate
                }()
                let controller = HotelDetailsModule.build(
                	withCode: hotelCode,
                	hotelBrand: searchSuggestion.brand,
                	existingAvailability: response,
                	bookingAllowed: true,
                	andSuggestion: suggestion,
                	delegate: delegate
                )

                return completion(true, controller)
            }

            guard let availabilities = manyAvailabilitiesResponse else { return completion(false, nil) }

            self.hotels = self.reordered(hotels: availabilities.hotels, forPriorityHotelWith: self.unavailableHotelCode)
			self.unavailableHotelCode = unavailableHotelCode

            completion(true, nil)
        }
    }

	var suggestionAndAllVenues: [Venue] {
		var results: [Venue] = []

		if let suggestion = suggestion {
			results.append(suggestion)
		}

		results.append(contentsOf: venues)

        if results.count > 1 {
            successfulSuggestion = suggestion
        }

		return results
	}

	var suggestionAndVenuesInReasonableDistance: [Venue] {
		var results: [Venue] = []

		guard let suggestion = suggestion else { return suggestionAndAllVenues }

		results.append(suggestion)

		let closeVenues: [Venue] = venues.compactMap {
			if MKMapPoint.init(suggestion.coordinate).distance(to: MKMapPoint.init($0.coordinate)) < Constants
			   .mapOverviewReasonableDistanceMeters { return $0 }
			return nil
		}

        guard closeVenues.isNotEmpty else { return suggestionAndFewVenues }

		results.append(contentsOf: Array(closeVenues.prefix((Constants.mapOverviewHotelsToShowMaximum - 1))))

		return results
	}

	func trackAvailability(style: VenuesListStyle) {
		guard let suggestion = suggestion else { return }
		guard let hotels = hotels else { return }

		// Criteria variables
		var data = BookingDetails.sharedInstance.criteria.analyticsDictionary

		// Trackable variables
		data[PIAnalytics.Keys.environment] = environment
		data[PIAnalytics.Keys.userLogin] = loggedIn.rawValue
		data[PIAnalytics.Keys.timeZone] = timeZone
		data[PIAnalytics.Keys.language] = language
		data[PIAnalytics.Keys.screenType] = screenType
        data[PIAnalytics.Keys.time] = Date().analyticsTimeFormat

        if let user = UserSessionManager.sharedInstance.currentUser, let companyId = user.companyId,
           let accessLevel = user.accessLevel?.rawValue {
            data[PIAnalytics.Keys.companyID] = companyId
            data[PIAnalytics.Keys.businessUserLevel] = accessLevel
        }

		// Current context variables
		data[PIAnalytics.Keys.searchLocation] = suggestion.title
		data[PIAnalytics.Keys.numResults] = "\(hotels.count)"
		data[PIAnalytics.Keys.searchType] = suggestion.isHotel ? "hotel" : "My location"
		data[PIAnalytics.Keys.sortType] = currentSorting == .distance ? "Distance" : "Price"

		if unavailableHotelCode != nil {
			data[PIAnalytics.Keys.noAvailability] = "1"
		}

		if hotels.isEmpty {
			data[PIAnalytics.Keys.event1] = 1
		}

        let productStrings: [String] = hotels.compactMap {
            let index = (hotels.firstIndex(of: $0) ?? 0) + 1

            return $0.searchResultsProductStringFromCheapestRate(atIndex: index)
        }
        for index in hotels.indices {
            data[PIAnalytics.Keys.getBookingSystem(index: index)] = PIAnalytics.HotelSource.opera.rawValue
            // Will need to uncomment the below and delete the above once opera availabilities is done
            // data[PIAnalytics.Keys.getBookingSystem(index: index)] = hotel.pmsSource.rawValue
        }
		data[PIAnalytics.Keys.productString] = productStrings.joined(separator: ",")
        data[PIAnalytics.Keys.pushToken] = AdobeCampaignManager.shared.apnsTokenString
		let screenName = style == .regular ? PIAnalytics.StateNames.searchResults : PIAnalytics.StateNames.searchResultsMap
        // DO NOT send for iPad as the list and map view appears simultaneously
        if UIDevice.current.userInterfaceIdiom != .pad {
            data[PIAnalytics.Keys.mapView] = style == .card
            data[PIAnalytics.Keys.listView] = style == .regular
        }
        analytics.trackState(screenName, data: data)
	}

	func logToFirebase() {
		guard let suggestion = suggestion else { return }

		let criteria = BookingDetails.sharedInstance.criteria

		var parameters: [String: NSObject] = [:]
		parameters[FirebaseAnalytics.Parameter.searchTerm] = (suggestion.title ?? "") as NSObject
		parameters[FirebaseAnalytics.Parameter.startDate] = criteria.arrivalDate.parameterString as NSObject
		parameters[FirebaseAnalytics.Parameter.endDate] = (criteria.checkOutDate?.parameterString ?? "") as NSObject
		parameters[FirebaseAnalytics.Parameter.numberOfNights] = criteria.nights as NSObject
		parameters[FirebaseAnalytics.Parameter.numberOfRooms] = criteria.rooms.count as NSObject
		parameters[FirebaseAnalytics.Parameter.numberOfPeople] = criteria.guestsCount as NSObject

        analytics.log(event: FirebaseAnalytics.Event.searchResults, parameters: parameters)
	}

    private func reordered(hotels: [Hotel]?, forPriorityHotelWith hotelCode: String?) -> [Hotel]? {
        guard var unorderedHotels = hotels else { return hotels }
        guard let code = hotelCode else { return unorderedHotels }
        guard let index = unorderedHotels.firstIndex(where: { $0.code == code }) else { return unorderedHotels }

        let hotel = unorderedHotels.remove(at: index)
        unorderedHotels.insert(hotel, at: 0)

        return unorderedHotels
    }

    func indexOfHotel(with code: String) -> Int? {
        hotels?.firstIndex(where: { $0.code == code })
    }

    func didTapDismissCoronavirusInformationBanner() {
        UserDefaults.standard.set(true, forKey: Constants.dismissedCoronavirusMessaging)
    }

    func shouldShowFallBack() -> Bool {
        SettingsManager.sharedInstance.shouldOperaRedirectToWeb
    }
}

extension VenuesInteractor: Trackable {
	var screenName: String { "" }
	var trackScreen: Bool { false }
	var environment: String { AnalyticsConstants.environment }
	var loggedIn: LoggedInAnalytic { UserSessionManager.sharedInstance.currentUser != nil ? .loggedIn : .notLoggedIn }
	var timeZone: String { TimeZone.current.description }
    var language: String { Locale.current.language.languageCode?.identifier ?? "n/a" }
	var screenType: String { PIAnalytics.StateTypes.lookToBook }
	var customParameters: [String: Any]? { nil }

	func applicationDidTakeScreenshot() {
	}
}
