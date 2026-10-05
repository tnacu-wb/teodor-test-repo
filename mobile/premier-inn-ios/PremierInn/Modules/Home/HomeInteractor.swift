//
//  HomeInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/06/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

enum HomeSearchError: Error {
    case hotelSearchResultNotAvailable
    case newAvailabilitiesFailed
    case availabilitySearchError
}

extension HomeSearchError: LocalizedError {
    var errorDescription: String? {
        switch self {
        case .hotelSearchResultNotAvailable:
            return PILocalizedString("searchResultsError", comment: "Search results error")
        case .newAvailabilitiesFailed, .availabilitySearchError:
            return PILocalizedString("searchResultsSearchFailedMessage", comment: "Search results error")
        }
    }
}

protocol HomeInteractorOutput {
    func startCheckInOnlineSession(
        with params: StartCheckInRequestParameters,
        completion: @escaping (_ response: CheckInOnlineSessionResponse?, _ error: Error?) -> Void
    )
}

extension RequestsManager: HomeInteractorOutput {}

protocol HomeInteractorInput: AnyObject, Trackable {
    var viewModel: HomeViewModel { get }
    var suggestion: Suggestion? { get set }
    var criteria: Criteria? { get set }
    var hasAcceptedGDPR: Bool { get }
    var hasShownBBCardExpiredMessage: Bool { get set }
    var shouldShowBBCardExpiredAlert: Bool { get }
    var bbRecentSearchErrorViewModel: (title: String, message: String) { get }
    var employeeRecentSearchErrorViewModel: (title: String, message: String) { get }
    var announcementMessage: NotificationsMessage? { get }
    func shouldShowBBRecentSearchError(forRecentSearchAt index: Int) -> Bool
    func shouldShowEmployeeRateSearchError(forRecentSearchAt index: Int) -> Bool
    func search(
        with suggestion: Suggestion,
        and criteria: Criteria,
        completion: @escaping (Result<UIViewController>) -> Void
    )
    func searchAvailability(
        hotelCode: String,
        hotelBrand: HotelBrand?,
        criteria: Criteria,
        completion: @escaping (Result<HotelAvailabilityResponse>) -> Void
    )
    func search(withRecentSearchAt index: Int, completion: @escaping (Result<UIViewController>) -> Void)
    func userDismissedCoronavirusInformationBanner()
    func startCheckInOnline(for stay: Stay, completion: @escaping (Bool, PreStayInputParams?) -> Void)
    func fetchHotelInformation(slug: String, completion: @escaping (Result<Hotel>) -> Void)
}

class HomeInteractor: NSObject {
    // MARK: - Properties

    private let requestsManager: RequestsManager = RequestsManager()
    private let coordinatesProvider = GoogleAPIDataProvider.sharedInstance
    private var searchTimeoutTimer: Timer?
    private var ciolFlowStarter: ReservationsInteractor?

    var dataProvider: HomeInteractorOutput?
    var analytics: AnalyticsType = AnalyticsManager.shared
    var suggestion: Suggestion?
    var criteria: Criteria? {
        get {
            BookingDetails.sharedInstance.criteria
        }
        set {
            guard let criteria = newValue else { return }
            BookingDetails.sharedInstance.criteria = criteria
        }
    }
    var hasAcceptedGDPR: Bool {
        UserDefaults.standard.bool(forKey: Constants.hasAcceptedGDPRChanges)
    }

    private var locationDescription: String {
        guard let title = suggestion?.title else { return PILocalizedString("CurrentLocation", comment: "") }
        return title
    }
    private var hasShownBBMessage = false

    private var nightsDescription: String {
        guard let criteria = criteria, let checkOutDate = criteria.checkOutDate else { return "-" }

        if criteria.arrivalDate.year == checkOutDate.year {
            return criteria.arrivalDate.localizedShortDayMonthStringFormat + " - " + checkOutDate
                .localizedShortDayMonthStringFormat
        } else {
            return criteria.arrivalDate.localizedShortDayMonthStringFormat + " - " + checkOutDate
                .localizedShortDayMonthYearStringFormat
        }
    }

    private var guestsDescription: String {
        guard let criteria = criteria else { return "NA guests/rooms" }
        return "\(criteria.guestsCountDescription), \(criteria.roomsCountDescription)"
    }

    private var companyName: String? {
        UserSessionManager.sharedInstance.currentUser?.company?.companyDetails?.alternateCompanyName ?? UserSessionManager
            .sharedInstance.currentUser?.company?.companyDetails?.companyName
    }

    deinit {
        searchTimeoutTimer?.invalidate()
    }

    @objc private func userDidChange() {
    }

    private func recentSearch(at index: Int) -> RecentSearch? {
        let recentSearchManager = RecentSearchesManager(dataSource: UserDefaults.standard)

        return recentSearchManager.recentSearch(at: index)
    }

    private func updateCoordinatesIfNeeded(
        for googlePlacesSuggestion: Suggestion,
        completion: @escaping (_ suggestion: Suggestion?, _ success: Bool) -> Void
    ) {
        guard googlePlacesSuggestion.coordinate.isValid else {
            updateCoordinates(for: googlePlacesSuggestion, completion: completion)
            return
        }

        completion(googlePlacesSuggestion, true)
    }

    private func updateCoordinates(
        for googlePlacesSuggestion: Suggestion,
        completion: @escaping (_ suggestion: Suggestion?, _ success: Bool) -> Void
    ) {
        guard let identifier = googlePlacesSuggestion.identifier else {
            completion(nil, false)
            return
        }

        coordinatesProvider.updateCoordinates(for: identifier) { coordinate, success in
            guard success, let coordinate = coordinate else { return completion(nil, false) }

            (googlePlacesSuggestion as? PISuggestion)?.coordinate = coordinate

            completion(googlePlacesSuggestion, true)
        }
    }
}

extension HomeInteractor: HomeInteractorInput {
    var viewModel: HomeViewModel {
        (
            PILocalizedString("landingMainMessage", comment: "Landing screen: main message"),
            locationDescription,
            nightsDescription,
            guestsDescription,
            UserDefaults.standard.bool(forKey: Constants.dismissedCoronavirusMessaging) == false && SettingsManager
            .sharedInstance.coronavirusMessagingHomepage,
            companyName
        )
    }

    var hasShownBBCardExpiredMessage: Bool {
        get {
            hasShownBBMessage
        }
        set {
            hasShownBBMessage = newValue
        }
    }
    var shouldShowBBCardExpiredAlert: Bool {
        guard !hasShownBBCardExpiredMessage else {
            return false
        }

        guard let company = UserSessionManager.sharedInstance.currentUser?.company else {
            return false
        }

        guard company.allowCentralCreditCard == true,
              company.bookingAllowances?.allowIndividualCards == false else {
            return false
        }

        guard let centrallyStoredCard = UserSessionManager.sharedInstance.currentUser?.centrallyStoredBusinessCard else {
            return false
        }

        return centrallyStoredCard.expired(onDate: Date())
    }

    var bbRecentSearchErrorViewModel: (title: String, message: String) {
        (
            PILocalizedString("Unable to search"),
            PILocalizedString("premierInnBusinessRoomLimitMessage")
        )
    }

    var employeeRecentSearchErrorViewModel: (title: String, message: String) {
        (
            PILocalizedString("Unable to search"),
            PILocalizedString("businessMaximumTwoRoomsMessage")
        )
    }

    func shouldShowBBRecentSearchError(forRecentSearchAt index: Int) -> Bool {
        guard BookingDetails.sharedInstance.bookingMode == .business else { return false }
        guard let result = recentSearch(at: index) else { return false }
        guard let criteria = result.criteria else { return false }

        return criteria.rooms.count > 1
    }

    func shouldShowEmployeeRateSearchError(forRecentSearchAt index: Int) -> Bool {
        guard BookingDetails.sharedInstance.employeeRatesEnabled else { return false }
        guard let result = recentSearch(at: index) else { return false }
        guard let criteria = result.criteria else { return false }

        return criteria.rooms.count > 2
    }

    var announcementMessage: NotificationsMessage? {
        guard hasAcceptedGDPR else { return nil }
        guard Feature.NotificationsMessage.isActive == true else { return nil }
        guard let message = SettingsManager.sharedInstance.notificationsMessage else { return nil }

        let lastReadId = UserDefaults.standard
            .integer(forKey: Constants.ImportantAnnouncements.lastImportantAnnouncementMessageReadIdKey)
        guard let messageId = message.id, messageId > lastReadId else { return nil }

        return message
    }

    private func searchOnlyLocations(
        with suggestion: Suggestion,
        and criteria: Criteria,
        completion: @escaping (Result<UIViewController>) -> Void
    ) {
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

        requestsManager.performSearch(with: searchSuggestion, criteria: criteria) { (nextController, error) in
            if let error = error {
                return completion(.failure(error: error))
            }

            guard let nextController = nextController
                else { return completion(.failure(error: HomeSearchError.hotelSearchResultNotAvailable)) }

            if let mapContainerController = nextController as? MapListContainerViewController, suggestion.isHotel == true {
                guard let hotelCode = suggestion.identifier else { return }
                mapContainerController.presenter?.setPreselectedHotel(with: hotelCode)
            }

            completion(.success(result: nextController))
        }
    }

    func search(
        with suggestion: Suggestion,
        and criteria: Criteria,
        completion: @escaping (Result<UIViewController>) -> Void
    ) {
        requestsManager.testForcePushValue = SettingsManager.sharedInstance.forceTestPushValue

        let wrappedCompletion = startSearchTimeout(completion: completion)

        updateCoordinatesIfNeeded(for: suggestion) { updatedSuggestion, _ in
            // if the update fails, use the original suggestion
            let suggestionToUse = updatedSuggestion ?? suggestion

            if UIDevice.current.userInterfaceIdiom == .pad {
                self.searchOnlyLocations(with: suggestionToUse, and: criteria, completion: wrappedCompletion)
                return
            }

            self.requestsManager.performSearch(with: suggestionToUse, criteria: criteria) { (nextController, error) in
                var error = error

                if error is MigrationServiceError {
                    // If the fallback error clear the error as the nextController has the alert on it
                    error = nil
                } else {
                    // If not the fallback error add the search to the recent searches
                    let recentSearchesManager = RecentSearchesManager(dataSource: UserDefaults.standard)
                    recentSearchesManager.add(suggestion, criteria: criteria)
                }

                if error != nil {
                    if suggestionToUse.isHotel == false {
                        return wrappedCompletion(.failure(error: HomeSearchError.newAvailabilitiesFailed))
                    } else {
                        return wrappedCompletion(.failure(error: HomeSearchError.availabilitySearchError))
                    }
                }

                guard let nextController = nextController
                    else { return wrappedCompletion(.failure(error: HomeSearchError.hotelSearchResultNotAvailable)) }

                wrappedCompletion(.success(result: nextController))
            }
        }
    }

    private func startSearchTimeout(completion: @escaping (Result<UIViewController>) -> Void)
        -> (Result<UIViewController>) -> Void {
        var hasCompleted = false
        let wrappedCompletion: (Result<UIViewController>) -> Void = { [weak self] result in
            guard !hasCompleted else { return }
            hasCompleted = true
            self?.searchTimeoutTimer?.invalidate()
            self?.searchTimeoutTimer = nil
            completion(result)
        }

        searchTimeoutTimer?.invalidate()
        searchTimeoutTimer = Timer
            .scheduledTimer(withTimeInterval: Constants.searchRequestTimeout, repeats: false) { [weak self] _ in
            self?.requestsManager.cancelConnections()
            DispatchQueue.main.async {
                wrappedCompletion(.failure(error: HomeSearchError.newAvailabilitiesFailed))
            }
        }

        return wrappedCompletion
    }

    func searchAvailability(
        hotelCode: String,
        hotelBrand: HotelBrand?,
        criteria: Criteria,
        completion: @escaping (Result<HotelAvailabilityResponse>) -> Void
    ) {
        BookingDetails.sharedInstance.criteria = criteria

        requestsManager.hotelAvailability(
            hotelCode: hotelCode,
            hotelBrand: hotelBrand,
            bookingDetails: BookingDetails.sharedInstance
        ) { [weak self] availabilityResponse, error in
            if let error = error {
                self?.analytics.track(error: error, name: PIAnalytics.Error.remoteAvailabilityLoadError)

                completion(.failure(error: error))
            }

            guard let availabilityResponse = availabilityResponse
                else { return completion(.failure(error: RequestsManagerError.unexpectedResponseError)) }
            guard availabilityResponse.rates.isNotEmpty
                else { return completion(.failure(error: RequestsManagerError.unexpectedResponseError)) }

            completion(.success(result: availabilityResponse))
        }
    }

    func search(withRecentSearchAt index: Int, completion: @escaping (Result<UIViewController>) -> Void) {
        guard let result = recentSearch(at: index) else { return }
        guard let criteria = result.criteria else { return }

        let suggestion = result as Suggestion

        self.search(with: suggestion, and: criteria, completion: completion)
    }

    func fetchHotelInformation(slug: String, completion: @escaping (Result<Hotel>) -> Void) {
        requestsManager.getHotelBySlug(slug: slug) { hotelInfo, error in
            if let error = error {
                completion(.failure(error: error))
                return
            }
            guard let hotelInfo = hotelInfo else {
                completion(.failure(error: RequestsManagerError.unexpectedResponseError))
                return
            }
            completion(.success(result: hotelInfo))
        }
    }

    func userDismissedCoronavirusInformationBanner() {
        UserDefaults.standard.set(true, forKey: Constants.dismissedCoronavirusMessaging)
    }

    private struct StartCheckInRequestParams: StartCheckInRequestParameters {
        let confirmationNumber: String
        let surname: String
        let arrivalDate: Date
        let guestHistoryNumber: String?
    }

    func startCheckIn(for stay: Stay, completion: @escaping (CheckInOnlineSessionResponse?, String?) -> Void) {
        guard let arrivalDate = stay.arrivalDate, let name = stay.importName else {
            completion(nil, CheckInError.missingRequiredParameters.localizedDescription)
            return
        }

        let params = StartCheckInRequestParams(
            confirmationNumber: stay.identifier,
            surname: name,
            arrivalDate: arrivalDate,
            guestHistoryNumber: UserSessionManager.sharedInstance.currentUser?.guestHistoryNumber
        )

        dataProvider?.startCheckInOnlineSession(with: params) { (response, error) in
            guard let response = response else {
                return completion(nil, error?.localizedDescription ?? CheckInError.genericError.localizedDescription)
            }
            guard !(response.paymentAllowed == false && response.paymentRequired == true) else {
                return completion(
                    nil,
                    error?.localizedDescription ?? CheckInError.notBookerWithPartialPayment.localizedDescription
                )
            }
            completion(response, nil)
        }
    }

    func startCheckInOnline(for stay: Stay, completion: @escaping (Bool, PreStayInputParams?) -> Void) {
        let flowStarter = ciolFlowStarter ?? ReservationsInteractor()
        flowStarter.stayToBeCheckedId = stay
        ciolFlowStarter = flowStarter

        flowStarter.startCheckInOnline { isSuccessful, preStayInputParams in
            completion(isSuccessful, preStayInputParams)
        }
    }
}

extension HomeInteractor: Trackable {
    var screenName: String { "" }
    var trackScreen: Bool { false }
    var environment: String { AnalyticsConstants.environment }
    var loggedIn: LoggedInAnalytic { UserSessionManager.sharedInstance.currentUser != nil ? .loggedIn : .notLoggedIn }
    var timeZone: String { TimeZone.current.description }
    var language: String { Locale.current.language.languageCode?.identifier ?? "n/a" }
    var screenType: String { PIAnalytics.StateTypes.home }
    var customParameters: [String: Any]? { nil }

    func applicationDidTakeScreenshot() { }
}
