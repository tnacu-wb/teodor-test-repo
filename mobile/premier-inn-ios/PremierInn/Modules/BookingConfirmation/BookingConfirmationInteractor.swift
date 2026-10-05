//
//  BookingConfirmationInteractor.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation
import MapKit
import EventKit
import SimpleNetwork
import PassKit

enum EventPermissionsError: LocalizedError {
	case permissionDenied
	case userDidNotGrantAccess
	case permissionRestricted
	case creationFailed
	case missingReservation
    case unknown

    var errorDescription: String? {
        switch self {
        case .creationFailed:
            return PILocalizedString(
                "calendarEventCreationFailureMessage",
                comment: "Calendar event creation failure message"
            )
        case .missingReservation:
            return PILocalizedString(
                "calendarEventCreationMissingReservation",
                comment: "Calendar event creation missing reservation failure message"
            )
        default:
            return String(describing: self)
        }
	}
}

enum BookingConfirmationRequestError: LocalizedError {
    case missingHotel
    case missingArrivalDate
    case reservationNonFound

    var errorDescription: String? {
        switch self {
        case .missingHotel:
            return PILocalizedString(
                "bookingConfirmationHotelDetailsLoadingErrorMessage",
                comment: "Booking confirmation hotel details loading error message"
            )
        case .missingArrivalDate:
            return PILocalizedString(
                "bookingConfirmationArrivalDateNotAvailableMessage",
                comment: "Booking confirmation arrival date not available message"
            )
        case .reservationNonFound:
            return PILocalizedString(
                "bookingConfirmationReservationNotAvailableMessage",
                comment: "Booking confirmation reservation not available error message"
            )
        }
    }
}

private struct StartCheckInRequestParams: StartCheckInRequestParameters {
    let confirmationNumber: String
    let surname: String
    let arrivalDate: Date
    let guestHistoryNumber: String?
}

public struct BookedUpsells {
    let availableUpsells: [BookedUpsellViewModel]?
    let unavailableUpsells: [BookedUpsellViewModel]?
}

extension RequestsManager: BookingConfirmationInteractorOutput {}

public enum ResendInvoiceStatus {
    case success
    case failed
    case notRequested
}

public typealias BookingConfirmationViewModelParams = (
    hotel: Hotel?,
    summary: Stay,
    reservation: Reservation?,
    bookedUpsells: BookedUpsells?,
    isCheckInOnlineAvailable: Bool,
    isCheckOutOnlineAvailable: Bool,
    shouldShowBannerInfo: Bool,
    shouldShowInfo: Bool,
    passSaved: Bool,
    isAmended: Bool,
    isOutOfDate: Bool,
    resendInvoiceStatus: ResendInvoiceStatus?
)

final class BookingConfirmationInteractor {
    typealias AnalyticsManagerType = (
        AnalyticsType &
        AnalyticsUpdateCiolStatusTrackable &
        AnalyticsThirdPartyBookingTrackable
    )

    var presenter: BookingConfirmationPresenterInput?
    var dataProvider: BookingConfirmationInteractorOutput? {
        didSet {
            backgroundFetchWalletPass()
        }
    }
    var isBookingFlowComplete: Bool
    var amendedStay: Bool = false
    var bookingDoesNotNeedToRefresh: Bool = false

    var pass: PKPass?
    private let walletPassManager: PassManagerProtocol
    private let settingsManager: SettingsManager
    private let analyticsManager: AnalyticsManagerType
    var isOutOfDate: Bool = false
    var resendInvoiceStatus: ResendInvoiceStatus = .notRequested
    var summary: Stay {
        didSet {
            guard amendedStay == true else { return }
            // update wallet if required
            forceUpdateWalletPass()
        }
    }
    var digitalKeyIdentifier: String?
    var reservation: Reservation?
    private(set) var hotel: Hotel?
    private(set) var preStayInputParams: PreStayInputParams?

    var bookedUpsells: BookedUpsells?

    deinit {
        dataProvider?.cancelConnections()
        NotificationCenter.default.removeObserver(self)
    }

    init(
        summary: Stay,
        isBookingFlowComplete: Bool,
        walletPassManager: PassManagerProtocol = PassManager(),
        settingsManager: SettingsManager = .sharedInstance,
        analyticsManager: AnalyticsManagerType = AnalyticsManager.shared
    ) {
        self.summary = summary
        self.isBookingFlowComplete = isBookingFlowComplete
        self.walletPassManager = walletPassManager
        self.settingsManager = settingsManager
        self.analyticsManager = analyticsManager

        self.updateLocalStoreWithDigitalKeyIds(stay: summary)

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(digitalKeyFlowDidClose),
            name: Notification.Name.digitalKeyFlowDidClose,
            object: nil
        )
    }

    func updateLocalStoreWithDigitalKeyIds(stay: Stay) {
        LocalReservationManager.shared.updateDigitalKeyIdentifiers(with: [StayWithKeyIdentifier(stay: stay)])
    }

    @objc func digitalKeyFlowDidClose(_ notification: Notification) {
        if let identifier = notification.object as? String {
            self.digitalKeyIdentifier = identifier
        }
    }

    var isCheckInOnlineAvailable: Bool {
        self.reloadStay()
        let stayValue = summary.isCheckInOnlineAvailable ?? false
        let featureEnabled = SettingsManager.sharedInstance.featureCIOL
        let result = stayValue && featureEnabled

        return result
    }

    var isCheckOutOnlineAvailable: Bool {
        self.reloadStay()

        return summary.isCheckOutOnlineAvailable ?? false
    }

    var roomKeyInstructionsModel: InstructionsViewModel? {
        guard let surname = summary.surname,
              let arrivalDate = summary.arrivalDate else { return nil }

        let type: InstructionsType = {
            switch (summary.qrCodeIsEnabled, summary.isDigitalKeyEnabled) {
            case (true, true):
                return .roomWithQRCodeAndDigitalKey(isKeyDownloaded: summary.userHasPassInWallet)
            case (false, true):
                return .roomWithoutQRCodeButWithDigitalKey(isKeyDownloaded: summary.userHasPassInWallet)
            case (true, false):
                return .roomKeyWithQRCode
            case (false, false):
                return .roomKeyWithoutQRCode
            }
        }()

        let reservationDetails = ReservationDetails(
            reservationId: summary.identifier,
            surname: surname,
            arrivalDate: arrivalDate,
            business: summary.business,
            token: nil
        )
        let savedPass = walletPassManager.getPass(
            withPassTypeIdentifier: Constants.pkPassTypeIdentifier,
            serialNumber: summary.pkPassSerialNumber
        )
        let model = InstructionsViewModel(
            type: type,
            bookingReference: summary.identifier,
            reservationDetails: reservationDetails,
            isQRCodeEnabled: summary.qrCodeIsEnabled,
            savedPass: savedPass
        )
        return model
    }

    private var shouldShowInfo: Bool {
        isBookingFlowComplete && !amendedStay
    }

    private var shouldShowBannerInfo: Bool {
        isReservationCancelled || amendedStay
    }

    private var walletReservationDetails: ReservationDetails? {
        // we need booking reference
        guard let surname = summary.surname, let arrivalDate = summary.arrivalDate else { return nil }
        let reservationDetails = ReservationDetails(
            reservationId: summary.identifier,
            surname: surname,
            arrivalDate: arrivalDate,
            business: summary.business,
            token: nil
        )

        return reservationDetails
    }

    private func backgroundFetchWalletPass() {
        guard self.pass == nil else { return } // pass already fetched
        guard walletPassManager.getPass(
            withPassTypeIdentifier: Constants.pkPassTypeIdentifier,
            serialNumber: summary.pkPassSerialNumber
        ) != nil else { return } // pass already in wallet
        guard let reservationDetails = walletReservationDetails else { return }

        dataProvider?.loadWalletPass(
            with: reservationDetails,
            isQRCodeEnabled: summary.qrCodeIsEnabled
        ) { [weak self] data, error in
            guard error == nil, let data = data else {
                printDev("error fetching wallet pass in the background")
                return
            }

            if let pass = try? self?.walletPassManager.makePass(from: data) {
                self?.pass = pass
            }
        }
    }

    private func forceUpdateWalletPass() {
        guard walletPassManager.getPass(
            withPassTypeIdentifier: Constants.pkPassTypeIdentifier,
            serialNumber: summary.pkPassSerialNumber
        ) != nil else {
            return
        }

        guard let reservationDetails = walletReservationDetails else { return }

        dataProvider?.loadWalletPass(
            with: reservationDetails,
            isQRCodeEnabled: summary.qrCodeIsEnabled
        ) { [weak self] data, error in
            guard let data = data,
                  error == nil,
                  let pass = try? self?.walletPassManager.makePass(from: data) else {
                return
            }

            self?.walletPassManager.replace(with: pass)
            self?.pass = pass
        }
    }
}

extension BookingConfirmationInteractor: BookingConfirmationInteractorInput {
    var customAnalyticsParameters: PIDictionary? {
        [PIAnalytics.Keys.bookingReference: summary.identifier]
    }

    var bookingConfirmationViewModel: BookingConfirmationViewModel? {
        var passSaved = false
        if let pass = walletPassManager.getPass(
            withPassTypeIdentifier: Constants.pkPassTypeIdentifier,
            serialNumber: summary.pkPassSerialNumber
        ) {
            self.pass = pass
            passSaved = true
        }

        let bookingConfirmationViewModelParams = BookingConfirmationViewModelParams(
            self.hotel,
            summary: self.summary,
            reservation: self.reservation,
            bookedUpsells: bookedUpsells,
            isCheckInOnlineAvailable: isCheckInOnlineAvailable,
            isCheckOutOnlineAvailable: isCheckOutOnlineAvailable,
            shouldShowBannerInfo: shouldShowBannerInfo,
            shouldShowInfo: shouldShowInfo,
            passSaved: passSaved,
            isAmended: amendedStay,
            isOutOfDate: isOutOfDate,
            resendInvoiceStatus: resendInvoiceStatus
        )

        return BookingConfirmationViewModel.createFrom(bookingConfirmationViewModelParams)
    }

    var isReservationCancelled: Bool { summary.cancelled }

    func fetchHotel() {
        dataProvider?.loadHotel(with: summary.hotelCode) { hotel, error in
            self.hotel = hotel

            if let error = error {
                self.presenter?.hotelFetchError(error: error)
                return
            }

            self.presenter?.hotelFetched()
        }
    }

    func fetchCalendarEvent(
        calendarPrompt: @escaping CalendarPrompt,
        completion: @escaping (EKEvent?, EKEventStore?, Error?) -> Void
    ) {
        guard let hotel = hotel else {
            completion(nil, nil, EventPermissionsError.missingReservation)
            return
        }

        SettingsManager.sharedInstance.emptyEventForBooking(
            showCalendarPrompt: calendarPrompt,
            completion: { store, event, error in
            if let error = error { return completion(nil, nil, error) }

            guard let event = event else { return completion(nil, nil, EventPermissionsError.creationFailed) }

            let reference = self.summary.identifier

            event.title = hotel.name + " #" + reference

            let mapItem: MKMapItem = {
                guard let placemark = hotel.placeMark else {
                    let address = hotel.address?.postalAddressDictionary
                    let placemark = MKPlacemark(coordinate: hotel.coordinate, addressDictionary: address)

                    return MKMapItem(placemark: placemark)
                }

                return MKMapItem(placemark: placemark)
            }()
            event.structuredLocation = EKStructuredLocation(mapItem: mapItem)
            event.startDate = self.summary.arrivalDate
            event.endDate = self.summary.checkOutDate

            completion(event, store, nil)
        }
        )
    }

    func reloadStay() {
        let manager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)

        guard let summary = manager.items.first(where: { $0.identifier == summary.identifier }) else { return }
        self.summary = summary
    }

    // swiftlint:disable:next function_body_length
    func loadLatestDetails(completion: @escaping (_ success: Bool) -> Void) {
        guard let arrivalDate = summary.arrivalDate else {
            completion(false)
            return
        }

        let surname = summary.importName ?? summary.lastName
        let reference = summary.identifier

        let findBookingDetails = FindBookingDetails(
            reservationId: reference,
            surname: surname,
            arrivalDate: arrivalDate,
            business: summary.isBusinessTrip
        )

        dataProvider?.findBookingSource(findBookingDetails: findBookingDetails) { source, error in
            guard let token = source?.token, let basketReference = source?.basketReference, error == nil else {
                return completion(false)
            }

            let reservationDetails = ReservationDetails(
                reservationId: basketReference,
                surname: surname,
                arrivalDate: arrivalDate,
                business: self.summary.isBusinessTrip,
                token: token
            )

            self.dataProvider?.reservation(
                reservationDetails: reservationDetails,
                hotelCode: self.summary.hotelCode,
                bookingDetails: nil
            ) { (reservation, error) in
                if let error = error {
                    return completion(false)
                }

                if let reservation = reservation {
                    self.reservation = reservation

                    let extractedDigitalKeyIdentifier = LocalReservationManager.shared
                        .getDigitalKeyIdentifierForReservation(reservation.confirmationNumber)

                    let dictionary = Stay.dictionary(
                        reservation: reservation,
                        hotel: self.hotel,
                        importType: ReservationImportType(rawValue: self.summary.importType) ?? ReservationImportType
                        .unknown,
                        importName: surname
                    )

                    guard let stay = try? Stay(dictionary: dictionary) else {
                        return completion(false)
                    }

                    // If the first room's reservationStatus is InHouse, this means this booking has been checked in at the front desk.
                    stay.stayBookingStatus = reservation.rooms.first?.bookingStatus == .checkedIn ? StayBookingStatus
                        .checkedIn : nil
                    stay.digitalKeyIdentifier = self.digitalKeyIdentifier ?? extractedDigitalKeyIdentifier
                    stay.numberOfAccessibleRooms = reservation.rooms.filter { $0.type == .accessible }.count
                    _ = LocalReservationManager.shared.update(with: [stay], sendUpdateNotification: false)

                    LocalReservationManager.shared.updateDigitalKeyIdentifiers(with: [StayWithKeyIdentifier(stay: stay)])

                    // we are setting this here but then getting overriden by the
                    // completion handler when we reloadViewModel() and then reloadStay()
                    // which picks the locally stored one
                    self.summary = stay
                    self.preStayInputParams = self.computePreStayInputParams(with: reservation)

                    ReservationsListViewController.shareDataWithTodayWidget()

                    guard let reservationId = stay.reservationIdentifier,
                          let hotelCode = self.hotel?.code else {
                        completion(false)
                        return
                    }
                    let bookingDetails = BookingDetails()
                    var criteria = Criteria()
                    criteria.nights = reservation.nights
                    criteria.arrivalDate = reservation.arrivalDate?.dateNormalised ?? Date()
                    bookingDetails.criteria = criteria
                    bookingDetails.rate = reservation.rate

                    self.dataProvider?.getPackages(
                        reservationId: reservationId,
                        bookingDetails: bookingDetails,
                        hotelCode: hotelCode,
                        bookingFlowId: stay.bookingFlowId,
                        showMealInclusiveRate: true
                    ) { [weak self] response, error in
                        guard let self = self, error == nil else {
                            completion(false)
                            return
                        }

                        let shouldFetchPaymentActions = preStayInputParams?.isDirect == false &&
                                                        settingsManager.featureThirdPartyPrepaid

                        analyticsManager.trackThirdPartyBookingIfRequired(
                            stay: summary,
                            reservation: reservation
                        )

                        if let response {
                            populateBookedUpsells(
                                hotelUpsells: response.0,
                                bookedUpsells: response.1,
                                nights: reservation.nights
                            )
                        }

                        if shouldFetchPaymentActions {
                            fetchPaymentActions(
                                basketReference: basketReference,
                                isUpsellResponseSuccess: response != nil,
                                completion: completion
                            )
                            return
                        }

                        completion(response != nil)
                    }
                } else {
                    completion(false)
                }
            }
        }
    }

    private func fetchPaymentActions(
        basketReference: String,
        isUpsellResponseSuccess: Bool,
        completion: @escaping (_ success: Bool) -> Void
    ) {
        dataProvider?.ciolPaymentActions(basketReference: basketReference) { [weak self] response, error in
            guard let self,
                  let response,
                  error == nil else {
                completion(false)
                return
            }

            preStayInputParams?.ciolPaymentActions = response

            completion(isUpsellResponseSuccess)
        }
    }

    func fetchWalletPass(
        completion: @escaping (WalletPassFetchResult) -> Void
    ) {
        if let pass {
            let state: WalletPassFetchResult =
                walletPassManager.contains(pass)
                ? .containsPass
                : .fetchSuccessful(pass)

            completion(state)
            return
        }

        guard let reservationDetails = walletReservationDetails else {
            completion(.noReservationDetails)
            return
        }

        dataProvider?.loadWalletPass(
            with: reservationDetails,
            isQRCodeEnabled: summary.qrCodeIsEnabled
        ) { [weak self] data, error in
            guard let self else {
                completion(.fetchFailed(EventPermissionsError.unknown))
                return
            }

            guard error == nil,
                  let data,
                  let pass = try? walletPassManager.makePass(from: data) else {
                completion(.fetchFailed(error))
                return
            }

            self.pass = pass

            let state: WalletPassFetchResult =
                walletPassManager.contains(pass)
                ? .containsPass
                : .fetchSuccessful(pass)

            completion(state)
        }
    }

    private func computePreStayInputParams(with reservation: Reservation) -> PreStayInputParams {
        BookingDetails.sharedInstance.hotel = hotel
        BookingDetails.sharedInstance.rate = reservation.rate
        BookingDetails.sharedInstance.isCiolBackgroundChargePerformedSuccessfully = false
        BookingDetails.sharedInstance.token = reservation.token
        BookingDetails.sharedInstance.basketReference = reservation.operaBasketReference
        BookingDetails.sharedInstance.roomLettings = reservation.rooms

        let hasDERegCard = reservation.rooms.allSatisfy { $0.deRegCardCompleted == true }
        let leadBookerModel = PreStayLeadBookerModel(reservation: reservation)

        return PreStayInputParams(
            hotel: hotel,
            flow: .bookingConfirmation,
            stay: summary,
            hotelAddress: hotel?.address?.description,
            hotelImage: hotel?.images.first?.image,
            hotelName: summary.hotelName,
            hotelCode: reservation.hotelCode,
            hotelBrand: hotel?.brand,
            reservationId: reservation.operaBasketReference,
            bookingFlowId: reservation.bookingFlowId,
            bookingReference: reservation.reservationIdentifier,
            arrivalDate: reservation.arrivalDate,
            checkOutDate: reservation.checkOutDate,
            adultsCountDescription: reservation.adultsCountDescription,
            adultsCount: reservation.adultsCount,
            childrenCountDescription: reservation.childrenCountDescription,
            childrenCount: reservation.childrenCount,
            nightsCountDescription: reservation.nightsCountDescription,
            nightsCount: reservation.nights,
            roomsCountDescription: reservation.roomsCountDescription,
            rooms: reservation.rooms,
            leadBookerTitle: leadBookerModel.title,
            leadBookerFirstName: leadBookerModel.firstName,
            leadBookerLastName: leadBookerModel.lastName,
            email: leadBookerModel.email,
            contactNumber: leadBookerModel.phoneNumber,
            address: leadBookerModel.address,
            outstandingBalance: reservation.outstandingAmount,
            bookingPreferences: reservation.bookingPreferences,
            isBusinessTrip: reservation.businessTrip,
            hasDERegCard: hasDERegCard,
            rateCode: reservation.rate?.code,
            rateDescription: reservation.rate?.description,
            isDirect: reservation.isDirect
        )
    }

    func resendInvoice(completion: @escaping () -> Void) {
        guard let reservation else {
            self.resendInvoiceStatus = .failed
            completion()
            return
        }

        dataProvider?.resendInvoiceEmail(reservation: reservation) { success in
            self.resendInvoiceStatus = success ? .success : .failed
            return completion()
        }
    }

    func performOnlineCheckout(completion: @escaping (Bool) -> Void) {
        guard let bookingReference = reservation?.operaBasketReference else { return }

        dataProvider?.confirmPreCheckInOut(basketReference: bookingReference, type: .checkOut, isCiol: false) { _, error in
            completion(error == nil)
        }
    }

    func updateCiolStatus(to ciolStatus: CiolStatus) {
        let payload = UpdateCiolStatusPayload(
            reservationIds: summary.roomIds,
            hotelId: summary.hotelCode,
            ciolStatus: ciolStatus
        )

        dataProvider?.updateCiolStatus(payload: payload) { [weak self] response, error in
            self?.analyticsManager.trackUpdateCiolStatus(
                screen: PIAnalytics.StateNames.bookingDetails,
                payload: payload,
                error: error,
                response: response
            )
        }
    }

    func trackAction(action: String) {
        var data: PIDictionary = [:]

        data[PIAnalytics.Keys.rooms] = summary.numberOfRoooms
        data[PIAnalytics.Keys.checkIn] = summary.arrivalDate?.analyticsDateFormat
        data[PIAnalytics.Keys.checkOut] = summary.checkOutDate?.analyticsDateFormat
        data[PIAnalytics.Keys.rateCode] = summary.rateClassification
        data[PIAnalytics.Keys.bookingID] = summary.identifier

        analyticsManager.trackAction(action, userInfo: data)
    }
}

extension BookingConfirmationInteractor: Trackable {
    var screenName: String { "" }
    var trackScreen: Bool { false }
    var environment: String { AnalyticsConstants.environment }
    var loggedIn: LoggedInAnalytic { UserSessionManager.sharedInstance.currentUser != nil ? .loggedIn : .notLoggedIn }
    var timeZone: String { TimeZone.current.description }
    var language: String { Locale.current.language.languageCode?.identifier ?? "n/a" }
    var screenType: String { PIAnalytics.StateTypes.bookingFlow }
    var customParameters: [String: Any]? { nil }

    func applicationDidTakeScreenshot() { }
}

// MARK: - Prepare Upsells

private extension BookingConfirmationInteractor {
    func populateBookedUpsells(
        hotelUpsells: [UpsellItem]?,
        bookedUpsells: [UpsellItem]?,
        nights: Int
    ) {
        guard let hotelUpsells = hotelUpsells,
              let bookedUpsells = bookedUpsells else {
            return
        }

        var unavailableUpsells = bookedUpsells.filter { upsell in
            !hotelUpsells.contains { $0.code == upsell.code}
        }

        unavailableUpsells = uniqueUpsells(unavailableUpsells)
        unavailableUpsells = mergeMealsWithKidsBreakfast(upsells: unavailableUpsells)

        var availableUpsells = bookedUpsells.filter { upsell in
            !unavailableUpsells.contains { $0.code == upsell.code }
        }

        availableUpsells = uniqueUpsells(availableUpsells)
        availableUpsells = mergeMealsWithKidsBreakfast(upsells: availableUpsells)

        let formattedUnavailableUpsells = unavailableUpsells.map {
            BookedUpsellViewModel(upsell: $0, isAvailable: false, nights: nights)
        }

        let formattedAvailableUpsells = availableUpsells.map {
            BookedUpsellViewModel(upsell: $0, isAvailable: true, nights: nights)
        }

        self.bookedUpsells = BookedUpsells(
            availableUpsells: formattedAvailableUpsells,
            unavailableUpsells: formattedUnavailableUpsells
        )

        reservation?.upsellItems = bookedUpsells
        reservation?.breakfasts = bookedUpsells
    }

    func uniqueUpsells(_ upsells: [UpsellItem]) -> [UpsellItem] {
        upsells.reduce((0, [UpsellItem]())) { accumulator, value in
            if accumulator.1.contains(where: { upsell in
                upsell.id == value.id &&
                upsell.id != UpsellItem.freeChildrensBreakfast.id
            }) {
                if var upsell = accumulator.1.first(where: { $0.code == value.code }),

                    let index = accumulator.1.firstIndex(where: { $0.code == value.code }) {
                    upsell.adults = (upsell.adults ?? 0) + (value.adults ?? 0)
                    upsell.children = (upsell.children ?? 0) + (value.children ?? 0)

                    upsell.roomId = [upsell.roomId ?? "", value.roomId ?? ""]
                        .filter { !$0.isEmpty }
                        .joined(separator: "/")

                    var copyOfAccumulator = accumulator
                    copyOfAccumulator.1[index] = upsell
                    return copyOfAccumulator
                } else {
                    return accumulator
                }
            } else {
                return (0, accumulator.1 + [value])
            }
        }.1
    }

    func mergeMealsWithKidsBreakfast(upsells: [UpsellItem]) -> [UpsellItem] {
        var updatedListOfUpsells = upsells
        let freeBreakfasts = upsells.filter({ $0.id == UpsellItem.freeChildrensBreakfast.id })

        freeBreakfasts.forEach { freeBreakfast in
            if let index = updatedListOfUpsells.firstIndex(where: {
                $0.foodUpsell && $0.freeBreakfastTrigger ?? false &&
                $0.roomId?.contains(freeBreakfast.roomId ?? "") ?? false
            }) {
                let updatedUpsellsChildCount = (updatedListOfUpsells[safe: index]?.children ?? 0)
                let freeBreakfastChildCount = (freeBreakfast.children ?? 0)
                updatedListOfUpsells[index].children = updatedUpsellsChildCount + freeBreakfastChildCount
            }
        }

        return updatedListOfUpsells.filter({ $0.id != UpsellItem.freeChildrensBreakfast.id })
    }
}
