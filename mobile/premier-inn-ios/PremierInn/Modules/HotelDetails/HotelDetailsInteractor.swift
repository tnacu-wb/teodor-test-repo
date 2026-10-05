//
//  HotelDetailsInteractor.swift
//  PremierInn
//
//  Automatically Created by Ophion
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

typealias SelectRateErrorAlertContent = (title: String, message: String, emailSubject: String?)

typealias HotelDetailsAnalytics = AnalyticsType & AnalyticsPromotionsTrackable

protocol HotelAvailabilityDataProvider {
    // swiftlint:disable:next function_parameter_count
    func loadInfoForHotel(
        withCode hotelCode: String?,
        hotelBrand: HotelBrand?,
        existingAvailability: HotelAvailabilityResponse?,
        andShouldCheckAvailability shouldCheckAvailability: Bool,
        datesChangedByUserInHdp: Bool,
        discountCodeViewModel: HotelDetailsDiscountCodeViewModel?,
        completion: @escaping (Result<Hotel>) -> Void
    )
}

extension RequestsManager: HotelAvailabilityDataProvider {}

enum SelectRateError: LocalizedError {
    case userRoleNotAllowedToMakeBookings
    case noPaymentMethods
    case genericPaymentMethod

    var localizedDescription: String {
        String(describing: self)
    }

    var alertContent: SelectRateErrorAlertContent {
        switch self {
        case .userRoleNotAllowedToMakeBookings:
            return (
                PILocalizedString("hotelDetailsBBStayerOnlyTitle"),
                PILocalizedString("hotelDetailsBBStayerOnlyMessage"),
                ""
            )
        case .noPaymentMethods:
            return (
                PILocalizedString("paymentMethodsNotAvailable"),
                "",
                UserSessionManager.sharedInstance.currentUser?
                .accessLevel == .superUser ? nil : PILocalizedString("hotelDetailsBBCardCantPrepayEmailContent")
            )
        case .genericPaymentMethod:
            return (PILocalizedString("paymentMethodsGenericError"), "", nil)
        }
    }
}

class HotelDetailsInteractor {
    private var requestsManager: RequestsManager = RequestsManager()
    private let availabilityProvider: HotelAvailabilityDataProvider
    private let analytics: HotelDetailsAnalytics
    var presenter: HotelDetailsPresenterProtocol?

    var _bookingAllowed: Bool
    private var _hotel: Hotel? {
        didSet {
            BookingDetails.sharedInstance.hotel = _hotel
            guard let distance = distanceToSearch else { return }
            _hotel?.distance = distance
        }
    }
    private var _hotelCode: String
    private var _hotelBrand: HotelBrand?
    private var _existingAvailability: HotelAvailabilityResponse?
    private var _hotelDetailsViewModel: HotelDetailsViewModel?
    private var _discountCodeViewModel: HotelDetailsDiscountCodeViewModel?
    private var _suggestion: Suggestion?
    private var _____shouldShowCheckAvailability: Bool
    private var _arrived_via_map_: Bool
    private var distanceToSearch: Double?
    private var _tripAdvisorViewModel: TripAdvisorViewModel?
    private var dismissedCoronavirusMessaging: Bool

    /// Cache the previously entered discount code to avoid repeating the API call
    private var previouslyUserEnteredDiscountCode: String?

    init(
        withCode hotelCode: String,
        hotelBrand: HotelBrand? = nil,
        bookingAllowed: Bool,
        existingAvailability: HotelAvailabilityResponse?,
        suggestion: Suggestion?,
        shouldShowCheckAvailability: Bool = false,
        arrivedViaMap: Bool = false,
        distanceToSearch: Double? = nil,
        availabilityProvider: HotelAvailabilityDataProvider? = nil,
        analytics: HotelDetailsAnalytics = AnalyticsManager.shared
    ) {
        _hotelCode = hotelCode
        _hotelBrand = hotelBrand
        _existingAvailability = existingAvailability
        _bookingAllowed = bookingAllowed
        _suggestion = suggestion
        _____shouldShowCheckAvailability = shouldShowCheckAvailability
        _arrived_via_map_ = arrivedViaMap
        dismissedCoronavirusMessaging = false

        self.distanceToSearch = distanceToSearch
        self.availabilityProvider = availabilityProvider ?? requestsManager
        self.analytics = analytics
    }
}

extension HotelDetailsInteractor: HotelDetailsInteractorProtocol {
    var viewModel: HotelDetailsViewModel? {
        if let tripAdvisorViewModel = self
           ._tripAdvisorViewModel { _hotelDetailsViewModel?.tripAdvisorViewModel = tripAdvisorViewModel }
        _hotelDetailsViewModel?.discountCodeViewModel = _discountCodeViewModel

        return _hotelDetailsViewModel
    }

    var hotel: Hotel? { _hotel }

    var phoneNumber: String? { hotel?.phoneNumber }

    var suggestion: Suggestion? { _suggestion }

    var carouselImageURLs: [URL] { hotel?.primaryImages.compactMap { $0.sizedImageURL(withSize: .large) } ?? [] }

    var roundelDesigns: [RoundelDesign]? { self.viewModel?.carouselViewModel.carouselRoundelDesigns }

    var accessibleRoomImages: [URL]? { hotel?.accessibleLoweredBathroomImages }

    var twinRoomImages: [URL]? { hotel?.alternativeTwinRoomImages }

    var criteria: Criteria { BookingDetails.sharedInstance.criteria }

    var arrivalDate: Date { BookingDetails.sharedInstance.criteria.arrivalDate }

    var numberOfNights: Int { BookingDetails.sharedInstance.criteria.nights }

    var bookingAllowed: Bool { _bookingAllowed }

    var screenName: String { PIAnalytics.StateNames.hotelDetails }

    var screenType: String { PIAnalytics.StateTypes.lookToBook }

    var shouldTrackScreen: Bool { false }

    /// Returns true if business booker, booking a hotel with authentication required, and only access to a centrally stored card which is not a BAC
    var shouldShowYouNeedToObtainCVVInformation: Bool {
        guard BookingDetails.sharedInstance.bookingMode == .business else { return false }
        guard hotel?.paymentProvider == .cccp else { return false }
        guard let company = UserSessionManager.sharedInstance.currentUser?.company else { return false }
        guard company.bookingAllowances?.allowIndividualCards == false else { return false }

        return UserSessionManager.sharedInstance.currentUser?.centrallyStoredBusinessCard?.isBusiness == false
    }

    var directionsViewModel: DirectionsViewModel? {
        guard let hotel = hotel else { return nil }
        return DirectionsViewModel.create(from: hotel)
    }

    var discountCodeViewModel: HotelDetailsDiscountCodeViewModel? {
        get { _discountCodeViewModel }
        set { _discountCodeViewModel = newValue }
    }

    var hasUpsells: Bool {
        guard let rate = BookingDetails.sharedInstance.rate else { return false }
        guard rate.hasUserPurchasableUpsells else { return false }
        guard let company = UserSessionManager.sharedInstance.currentUser?.company else { return true }
        guard let allowedCompanyUpsells = company.bookingAllowances?.upsellItemsAllowed else { return false }

        let allowedCompanyUpsellsCodes = allowedCompanyUpsells.compactMap { Int($0) }

        let hasECILCO = rate.extraUpsells?
            .contains(where: { $0.upsellOperaId == .earlyCheckIn || $0.upsellOperaId == .lateCheckOut }) ?? false

        let isUltimateWifiIncluded = company.isUltimateWifiAllowed

        return rate.upsellsAvailable(for: allowedCompanyUpsellsCodes) || hasECILCO || isUltimateWifiIncluded
    }

    var userIsAllowedToMakeBookings: Bool {
        guard let accessLevel = UserSessionManager.sharedInstance.currentUser?.accessLevel else { return true }

        return accessLevel != .stayer
    }

    func updateBookingAllowed(to bookingAllowed: Bool) {
        _bookingAllowed = bookingAllowed
    }

    func applyUserEnteredDiscountCodeIfNeeded() {
        guard let _discountCodeViewModel,
              previouslyUserEnteredDiscountCode != _discountCodeViewModel.validDiscountCode else {
            return
        }

        presenter?.showLoadingIndicator()
        BookingDetails.sharedInstance.userEnteredPromoCode = _discountCodeViewModel.validDiscountCode ?? String()
        BookingDetails.sharedInstance.promoKind = _discountCodeViewModel.validDiscountCodeType
        loadAvailability(datesChanged: false)
        previouslyUserEnteredDiscountCode = _discountCodeViewModel.validDiscountCode
    }

    func loadAvailability(datesChanged: Bool) {
        availabilityProvider.loadInfoForHotel(
            withCode: _hotelCode,
            hotelBrand: _hotelBrand,
            existingAvailability: _existingAvailability,
            andShouldCheckAvailability: _bookingAllowed,
            datesChangedByUserInHdp: datesChanged,
            discountCodeViewModel: _discountCodeViewModel
        ) { [weak self] result in
            switch result {
            case .success(let hotel):
                self?._hotel = hotel
                self?.logFirebase(withHotel: hotel)
                self?.logAvailabilityDynatrace()
                self?.updateViewModel(with: hotel)
                self?.trackAvailability(withHotel: hotel, availabilityChecked: self?._bookingAllowed == true)
                self?.presenter?.hotelUpdated()
                self?.setPromoRateTagsInBookingDetailsIfNeeded(withHotel: hotel)

            case .failure(let error):
                printDev(error)

                self?.analytics.track(error: error, name: PIAnalytics.Error.remoteAvailabilityLoadError)
                self?.presenter?.hotelUpdateFailed(with: error)
            }
        }
    }

    private func updateViewModel(with hotel: Hotel) {
        _hotelDetailsViewModel = HotelDetailsViewModel.createFrom(
            hotelDetailsCreateParams: HotelDetailsCreateParams(
                hotel,
                criteria: BookingDetails.sharedInstance.criteria,
                _suggestion,
                discountCodeViewModel: _discountCodeViewModel,
                shouldShowCheckAvailability: _____shouldShowCheckAvailability.reset(),
                bookingAllowed: bookingAllowed,
                dismissedCoronavirusMessaging: dismissedCoronavirusMessaging
            )
        )
        presenter?.hotelUpdated()
    }

    private func setPromoRateTagsInBookingDetailsIfNeeded(withHotel hotel: Hotel?) {
        if let promoRate = hotel?.promotionRate {
            let promoRateTags = promoRate.promotionTag(for: hotel?.brand)
            BookingDetails.sharedInstance.promoRateTags = promoRateTags
        }
    }

    func updateSuggestion(to suggestion: Suggestion) {
        _suggestion = suggestion
    }


    func trackAvailability(withHotel hotel: Hotel? = nil, hotelCode: String? = nil, availabilityChecked: Bool) {
        var data: PIDictionary = analytics.analyticsProperties(stateType: PIAnalytics.StateTypes.bookingFlow)

        if let suggestion = suggestion {
            data[PIAnalytics.Keys.searchLocation] = suggestion.title
            data[PIAnalytics.Keys.searchType] = {
                if _arrived_via_map_ == true {
                    return "Map"
                }
                return suggestion.title == PILocalizedString("userCoordinateName") ?
                "Near Me" :
                "Location"
            }()
        } else {
            data[PIAnalytics.Keys.searchType] = "Hotel"
        }

        if let hotel = hotel {
            data[PIAnalytics.Keys.productString] = hotel.hotelDetailsProductString(includeDistance: true)
        } else if let hotelCode = hotelCode {
            data[PIAnalytics.Keys.productString] = ";\(hotelCode)"
        }

        data[PIAnalytics.Keys.promoBoxVisible] = _hotelDetailsViewModel?.shouldShowAddADiscountCodeSection ?? false

        if availabilityChecked == true {
            if hotel?.isAvailableAndHasRates == false {
                data[PIAnalytics.Keys.noAvailability] = "1"
            }

            if let promotionsData = analytics.getPromotionsAnalyticsDict(with: BookingDetails.sharedInstance) {
                data.mergePreferNew(promotionsData)
            }

            data = data.merging(
                BookingDetails.sharedInstance.criteria.analyticsDictionary,
                uniquingKeysWith: { (_, last) in last }
            )
        } else {
            data[PIAnalytics.Keys.event2] = "1"
        }

        data[PIAnalytics.Keys.prodView] = "1"
        data[PIAnalytics.Keys.getBookingSystem(index: 0)] = PIAnalytics.HotelSource.opera.rawValue
        // Will need to uncomment the below and delete the above once opera availabilities is done
        // data[PIAnalytics.Keys.getBookingSystem(index: 0)] = hotel.pmsSource.rawValue
        data[PIAnalytics.Keys.pushToken] = AdobeCampaignManager.shared.apnsTokenString
        analytics.trackState(
            availabilityChecked == false ? PIAnalytics.StateNames.hotelDetailsUncheckedAvailability : screenName,
            data: data
        )
    }

    func logFirebase(withHotel hotel: Hotel?) {
        let criteria = BookingDetails.sharedInstance.criteria

        var parameters: [String: NSObject] = [:]
        parameters[FirebaseAnalytics.Parameter.hotelCode] = (hotel?.code ?? "") as NSObject
        parameters[FirebaseAnalytics.Parameter.searchTerm] = (suggestion?.title ?? "") as NSObject
        parameters[FirebaseAnalytics.Parameter.startDate] = criteria.arrivalDate.parameterString as NSObject
        parameters[FirebaseAnalytics.Parameter.endDate] = (criteria.checkOutDate?.parameterString ?? "") as NSObject
        parameters[FirebaseAnalytics.Parameter.numberOfNights] = criteria.nights as NSObject
        parameters[FirebaseAnalytics.Parameter.numberOfRooms] = criteria.rooms.count as NSObject
        parameters[FirebaseAnalytics.Parameter.numberOfPeople] = criteria.guestsCount as NSObject

        AnalyticsManager.shared.log(event: FirebaseAnalytics.Event.hotelDetail, parameters: parameters)
    }

    func logAvailabilityDynatrace() {
        AnalyticsManager.shared.trackDTMetric(
            double: 1.0,
            actionName: DynatraceActionName.availabilityLoaded,
            actionKey: DynatraceActionKey.availabilityKey
        )
    }

    func datesChanged(withNewArrivalDate newArrivalDate: Date, andNumberOfNights numberOfNights: Int) {
        dismissedCoronavirusMessaging = false
        BookingDetails.sharedInstance.criteria.arrivalDate = newArrivalDate
        BookingDetails.sharedInstance.criteria.nights = numberOfNights
        BookingDetails.sharedInstance.userEnteredPromoCode = nil
    }

    func criteriaChanged(to newCriteria: Criteria) {
        BookingDetails.sharedInstance.criteria = newCriteria
    }

    func roomsForRateID(_ rateID: UUID, and lettingType: String?) -> [Room] {
        guard let hotel = hotel else { return [Room]() }
        guard let rooms = hotel.rates.first(where: { $0.uniqueID == rateID })?.rooms else { return [Room]() }

        guard let roomClass = rooms.flatMap({ $0.options ?? [] }).first(where: { $0.lettingType == lettingType })?.roomClass
            else { return [Room]() }

        return rooms.map { room in
            guard let roomCopy = room.copy() as? Room else { return room }

            // for each room keep the options of the selected room class
            roomCopy.options = room.options?.filter({ $0.roomClass == roomClass })

            return roomCopy
        }
    }

    func holdBooking(withRateID rateID: UUID, completion: @escaping (Bool) -> Void) {
        requestsManager.performHoldBooking(bookingDetails: BookingDetails.sharedInstance) { basketReference, error in
            guard let basketReference = basketReference, error == nil else {
                return completion(false)
            }
            self.requestsManager.bookingInformation(
                with: basketReference,
                isBusiness: BookingDetails.sharedInstance.bookingMode == .business
            ) { bookingInformation, _ in
                guard let bookingFlowId = bookingInformation?.bookingFlowId,
                      let hotelCode = BookingDetails.sharedInstance.hotel?.code, error == nil else {
                    AnalyticsManager.shared.log(
                        event: FirebaseAnalytics.Event.failedToGetBookingFlowId,
                        parameters: AnalyticsManager.getPackagesErrorParams(bookingDetails: BookingDetails.sharedInstance)
                    )
                    return completion(true)
                }
                self.requestsManager.getPackages(
                    reservationId: basketReference,
                    bookingDetails: BookingDetails.sharedInstance,
                    hotelCode: hotelCode,
                    bookingFlowId: bookingFlowId
                ) { response, _ in
                    guard let upsells = response?.0 else {
                        AnalyticsManager.shared.log(
                            event: FirebaseAnalytics.Event.failedToGetPackages,
                            parameters: AnalyticsManager
                            .getPackagesErrorParams(bookingDetails: BookingDetails.sharedInstance)
                        )
                        return completion(true)
                    }

                    if let addedUpsells = response?.1 {
                        BookingDetails.sharedInstance.loadPreselectedRateUpsells(upsells: addedUpsells)
                    }

                    BookingDetails.sharedInstance.rate?.upsellItems = upsells
                    BookingDetails.sharedInstance.hotel?.cityTaxForLeisure = response?.2.cityTaxForLeisure
                    BookingDetails.sharedInstance.hotel?.cityTaxForBusiness = response?.2.cityTaxForBusiness
                    BookingDetails.sharedInstance.goshOptions = response?.3

                    return completion(true)
                }
            }
        }
    }

    func setCriteria(withRateID rateID: UUID, and lettingType: String?) {
        guard let hotel = hotel else { return }

        let ratesFound = hotel.rates.filter({ $0.uniqueID == rateID })
        guard ratesFound.count == 1 else { return }
        guard let rate = ratesFound.first else { return }

        BookingDetails.sharedInstance.hotel = hotel
        BookingDetails.sharedInstance.bookingFlowHoldTime = Date()
        BookingDetails.sharedInstance.meal = nil
        BookingDetails.sharedInstance.roomMealCombos = {
            guard let userMealPreference = UserSessionManager.sharedInstance.currentUser?.bookingPreference?.foodPreference
                else { return nil }
            guard let upsellItems = rate.upsellItems else { return nil }
            let upsellItemsFiltered = hotel.upsellsAvailable(
                arrivalDate: BookingDetails.sharedInstance.criteria.arrivalDate,
                departureDate: BookingDetails.sharedInstance.criteria.checkOutDate,
                upsells: upsellItems
            )
            guard let meal = upsellItemsFiltered.first(where: { $0.code == userMealPreference.rawValue }) else { return nil }
            let quantity = BookingDetails.sharedInstance.criteria.rooms.first?.adults ?? 1

            return [(0, meal, quantity)]
        }()
        BookingDetails.sharedInstance.rate = rate
        BookingDetails.sharedInstance.roomLettings = roomsForRateID(rateID, and: lettingType)
        BookingDetails.sharedInstance.updatePaymentMethodIfRequired(for: rate)
    }

    func shouldShowEmployeeOfferInformation(for rateID: UUID) -> Bool {
        guard let hotel = hotel else { return false }

        let ratesFound = hotel.rates.filter({ $0.uniqueID == rateID })
        guard ratesFound.count == 1 else { return false }
        guard let rate = ratesFound.first else { return false }

        return rate.classification == SimpleNetwork.Constants.EmployeeOffer.rateClassification
    }

    func customerDismissedCoronavirusInformation() {
        dismissedCoronavirusMessaging = true

        if let hotel = self.hotel {
            updateViewModel(with: hotel)
        }

        presenter?.hotelUpdated()
    }

    func validateUserCanBookRate(with id: UUID, completion: @escaping (Result<Bool>) -> Void) {
        guard userIsAllowedToMakeBookings else {
            completion(.failure(error: SelectRateError.userRoleNotAllowedToMakeBookings))
            return
        }

        guard let hotel = hotel else {
            completion(.failure(error: PaymentOptionsError.missingHotelCode))
            return
        }

        guard hotel.rates.first(where: { $0.uniqueID == id }) != nil else {
            completion(.failure(error: PaymentOptionsError.missingRateRules))
            return
        }

        completion(.success(result: true))
    }

    func userLoggedOut() {
        UserSessionManager.sharedInstance.piUserLoggedOut()

        SettingsManager.sharedInstance.shouldAttemptAutoLogin = false
        RequestsManager.removeStays()
    }
}
