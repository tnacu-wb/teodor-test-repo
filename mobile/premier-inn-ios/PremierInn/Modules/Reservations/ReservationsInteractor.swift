//
//  ReservationsInteractor.swift
//  PremierInn
//
//  Created by Marcello Mascia on 07/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import PassKit

enum CheckInError: LocalizedError {
    case missingRequiredParameters
    case notBookerWithPartialPayment
    case genericError

    var localizedDescription: String {
        switch self {
        case .missingRequiredParameters:
            return PILocalizedString("Could not start check-in because we couldn't find the right name or arrival date")
        case .notBookerWithPartialPayment:
            return PILocalizedString(
                "Online check-in is only available to the booker, as partial payment has already been made."
            )
        default:
            return PILocalizedString("Something went wrong")
        }
    }
}

protocol ReservationsInteractorProtocol: Trackable {
    var customAnalyticsParameters: PIDictionary? { get }
    var stayToBeCheckedId: Stay? { get set }
    func getRoomKeyInstructionsModel(stay: Stay) -> InstructionsViewModel?
    func listenToReservationChanges(with: @escaping (Result<ActivePastStays>) -> Void)
    func refreshStays(completion: @escaping (Bool?) -> Void)
    func startCheckInOnline(completion: @escaping (Bool, PreStayInputParams?) -> Void)
    func updateCiolStatus(forStay stay: Stay?, to ciolStatus: CiolStatus)
}

protocol ReservationsInteractorDataProvider: LabelsProvider {
    func cancelConnections()
    func refreshStays(for user: User?, shouldAttemptLogin: Bool, completion: @escaping (Bool?) -> Void)
    func loadHotel(with hotelCode: String, completion: @escaping (Hotel?, Error?) -> Void)
    func findBookingSource(
        findBookingDetails: FindBookingDetails,
        completion: @escaping (FindBookingSource?, Error?) -> Void
    )
    func reservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        bookingDetails: BookingDetails?,
        completion: @escaping (Reservation?, Error?) -> Void
    )
    func updateCiolStatus(
        payload: UpdateCiolStatusPayload,
        completion: @escaping (_ response: UpdateCiolStatusResponse?, _ error: Error?) -> Void
    )
    func ciolPaymentActions(
        basketReference: String,
        completion: @escaping (_ response: CiolPaymentActionsResponse?, _ error: Error?) -> Void
    )
}

extension RequestsManager: ReservationsInteractorDataProvider { }

private struct StartCheckInRequestParams: StartCheckInRequestParameters {
    let confirmationNumber: String
    let surname: String
    let arrivalDate: Date
    let guestHistoryNumber: String?
}

typealias ActivePastStays = (headerMessage: String?, activeStays: [Stay], pastStays: [Stay], importAction: Bool)

class ReservationsInteractor {
    typealias AnalyticsManagerType = (
        AnalyticsUpdateCiolStatusTrackable &
        AnalyticsThirdPartyBookingTrackable
    )

    deinit {
        NotificationCenter.default.removeObserver(self)
        dataProvider.cancelConnections()
        completion = nil
        print("DEINIT: \(self)")
    }

    private let dataProvider: ReservationsInteractorDataProvider
    private let settingsManager: SettingsManager
    private let analyticsManager: AnalyticsManagerType
    private var completion: ((Result<ActivePastStays>) -> Void)?
    var stayToBeCheckedId: Stay?

    init(
        with dataProvider: ReservationsInteractorDataProvider = RequestsManager(),
        settingsManager: SettingsManager = .sharedInstance,
        analyticsManager: AnalyticsManagerType = AnalyticsManager.shared
    ) {
        self.dataProvider = dataProvider
        self.settingsManager = settingsManager
        self.analyticsManager = analyticsManager
    }

    @objc private func userDidChange(notification: Notification?) {
        let didImportBookings = notification?
            .userInfo?[SimpleNetwork.Constants.NotificationKeys.reservationSummariesWereImported] as? Bool ?? false

        triggerCompletionBlock(importAction: didImportBookings)
    }

    @objc private func reservationSummariesDidChange(notification: Notification?) {
        let didImportBookings = notification?
            .userInfo?[SimpleNetwork.Constants.NotificationKeys.reservationSummariesWereImported] as? Bool ?? false

        triggerCompletionBlock(importAction: didImportBookings)
    }

    private func triggerCompletionBlock(importAction: Bool) {
        let stays = currentStays
        // ignore the importAction for now because it is always true, even if we just logged in
        // refreshed my bookings etc, and not only when you import a booking
        completion?(Result.success(result: (nil, stays.activeStays, stays.pastStays, false)))
    }

    private var currentStays: ActivePastStays {
        let reservationsManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)
        let stays = reservationsManager.items

        let activeStays = stays.filter {
            guard $0.cancelled == false else { return false }
            guard let checkOutDate = $0.checkOutDate else { return false }

            return checkOutDate >= Date() || checkOutDate.isToday
            }.sorted { customSorting(lhs: $0, rhs: $1) }

        let pastStays = stays.difference(from: activeStays).sorted { customSorting(lhs: $0, rhs: $1) }

        return (nil, activeStays, pastStays, false)
    }

    private func customSorting(lhs: Stay, rhs: Stay) -> Bool {
        guard let date1 = lhs.arrivalDate else { return false }
        guard let date2 = rhs.arrivalDate else { return false }

        if date1.isOnTheSameDateAs(date: date2) {
            return lhs.hotelName < rhs.hotelName
        }

        return date1 < date2
    }
}

extension ReservationsInteractor: ReservationsInteractorProtocol {
    var customAnalyticsParameters: PIDictionary? {
        guard UserSessionManager.sharedInstance.currentUser != nil else {
            return nil
        }

        var customParameters = PIDictionary()

        let stays = currentStays
        let cancelledBookings = stays.pastStays.filter({ $0.cancelled }).count
        customParameters[PIAnalytics.Keys.dashboardCancelledBookings] = cancelledBookings
        let checkedinBookings = stays.activeStays.filter({ $0.checkedIn }).count
        customParameters[PIAnalytics.Keys.dashboardCheckedinBookings] = checkedinBookings
        let stayedBookings = stays.pastStays.filter({ !$0.cancelled }).count
        customParameters[PIAnalytics.Keys.dashboardStayedBookings] = stayedBookings
        let futureBookings = stays.activeStays.filter({ !$0.checkedIn && $0.isUpcoming }).count
        customParameters[PIAnalytics.Keys.dashboardFutureBookings] = futureBookings
        let totalBookings = stays.activeStays.count + stays.pastStays.count
        customParameters[PIAnalytics.Keys.dashboardTotalBookings] = totalBookings
        let moreThanFourRooms = stays.activeStays.filter({ $0.numberOfRoooms > 4 }).count + stays.pastStays
            .filter({ $0.numberOfRoooms > 4 }).count
        customParameters[PIAnalytics.Keys.dashboardMoreThanFourRooms] = moreThanFourRooms
        let moreThanNineNights = stays.activeStays.filter({ $0.numberOfNights > 9 }).count + stays.pastStays
            .filter({ $0.numberOfNights > 9 }).count
        customParameters[PIAnalytics.Keys.dashboardMoreThanNineNights] = moreThanNineNights

        return customParameters
    }

    func getRoomKeyInstructionsModel(stay: Stay) -> InstructionsViewModel? {
        guard let surname = stay.surname,
              let arrivalDate = stay.arrivalDate else {
            return nil
        }

        let type: InstructionsType = {
            switch (stay.qrCodeIsEnabled, stay.isDigitalKeyEnabled) {
            case (true, true):
                return .roomWithQRCodeAndDigitalKey(isKeyDownloaded: stay.userHasPassInWallet)
            case (false, true):
                return .roomWithoutQRCodeButWithDigitalKey(isKeyDownloaded: stay.userHasPassInWallet)
            case (true, false):
                return .roomKeyWithQRCode
            case (false, false):
                return .roomKeyWithoutQRCode
            }
        }()

        let reservationDetails = ReservationDetails(
            reservationId: stay.identifier,
            surname: surname,
            arrivalDate: arrivalDate,
            business: stay.business,
            token: nil
        )
        let savedPass = PKPassLibrary().pass(
            withPassTypeIdentifier: Constants.pkPassTypeIdentifier,
            serialNumber: stay.pkPassSerialNumber
        )
        let model = InstructionsViewModel(
            type: type,
            bookingReference: stay.identifier,
            reservationDetails: reservationDetails,
            isQRCodeEnabled: stay.qrCodeIsEnabled,
            savedPass: savedPass
        )
        return model
    }

    func listenToReservationChanges(with completion: @escaping (Result<ActivePastStays>) -> Void) {
        self.completion = completion

        NotificationCenter.default.addObserver(self, selector: #selector(userDidChange), name: .userDidChange, object: nil)
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(reservationSummariesDidChange),
            name: .reservationSummariesDidChange,
            object: nil
        )
    }

    func refreshStays(completion: @escaping (Bool?) -> Void) {
        dataProvider.refreshStays(
            for: UserSessionManager.sharedInstance.currentUser,
            shouldAttemptLogin: true,
            completion: completion
        )
    }

    /// Before actually starting the CIOL flow we need to fetch information needed for CIOL
    /// For this we need to call the following APIs
    /// hotelInformation, findBookingSource and bookingConfirmation
    func startCheckInOnline(completion: @escaping (Bool, PreStayInputParams?) -> Void) {
        guard let arrivalDate = stayToBeCheckedId?.arrivalDate,
              let surname = stayToBeCheckedId?.importName ?? stayToBeCheckedId?.lastName,
              let reference = stayToBeCheckedId?.identifier,
              let hotelCode = stayToBeCheckedId?.hotelCode else {
            completion(false, nil)
            return
        }

        dataProvider.loadHotel(with: hotelCode) { [weak self] hotel, error in
            guard let self = self,
                  let hotel = hotel else { return completion(false, nil) }
            let findBookingDetails = FindBookingDetails(
                reservationId: reference,
                surname: surname,
                arrivalDate: arrivalDate,
                business: stayToBeCheckedId?.isBusinessTrip ?? false
            )

            dataProvider.findBookingSource(findBookingDetails: findBookingDetails) { [weak self] source, error in
                guard let self = self, let token = source?.token,
                      let basketReference = source?.basketReference,
                      error == nil else { return completion(false, nil) }

                let reservationDetails = ReservationDetails(
                    reservationId: basketReference,
                    surname: surname,
                    arrivalDate: arrivalDate,
                    business: stayToBeCheckedId?.isBusinessTrip ?? false,
                    token: token
                )

                dataProvider.reservation(
                    reservationDetails: reservationDetails,
                    hotelCode: hotelCode,
                    bookingDetails: nil
                ) { [weak self] (reservation, _) in
                    guard let self, let reservation = reservation else {
                        return completion(false, nil)
                    }

                    analyticsManager.trackThirdPartyBookingIfRequired(
                        stay: stayToBeCheckedId,
                        reservation: reservation
                    )

                    if !reservation.isDirect && settingsManager.featureThirdPartyPrepaid {
                        fetchCiolPaymentActions(
                            reservation: reservation,
                            hotel: hotel,
                            basketReference: basketReference,
                            completion: completion
                        )
                        return
                    }

                    guard let preStayInputParams = computePreStayInputParams(with: reservation, hotel: hotel) else {
                        completion(false, nil)
                        return
                    }

                    updateCiolStatus(forStay: stayToBeCheckedId, to: .ciolStarted)

                    completion(true, preStayInputParams)
                }
            }
        }
    }

    func fetchCiolPaymentActions(
        reservation: Reservation,
        hotel: Hotel,
        basketReference: String,
        completion: @escaping (Bool, PreStayInputParams?) -> Void
    ) {
        dataProvider.ciolPaymentActions(basketReference: basketReference) { [weak self] response, error in
            guard let self,
                  let response,
                  error == nil else {
                completion(false, nil)
                return
            }

            guard let preStayInputParams = computePreStayInputParams(
                with: reservation,
                hotel: hotel,
                paymentActions: response
            ) else {
                completion(false, nil)
                return
            }

            updateCiolStatus(forStay: stayToBeCheckedId, to: .ciolStarted)

            completion(true, preStayInputParams)
        }
    }

    private func computePreStayInputParams(
        with reservation: Reservation,
        hotel: Hotel,
        paymentActions: CiolPaymentActionsResponse? = nil
    ) -> PreStayInputParams? {
        BookingDetails.sharedInstance.hotel = hotel
        BookingDetails.sharedInstance.rate = reservation.rate
        BookingDetails.sharedInstance.isCiolBackgroundChargePerformedSuccessfully = false
        BookingDetails.sharedInstance.token = reservation.token
        BookingDetails.sharedInstance.basketReference = reservation.operaBasketReference
        BookingDetails.sharedInstance.roomLettings = reservation.rooms

        guard let stay = stayToBeCheckedId else { return nil }
        stay.isDigitalKey = reservation.isDigitalKey
        stay.reservationPackageList = reservation.reservationPackageList
        stay.bookerEmail = reservation.booker?.emailAddress

        let hasDERegCard = reservation.rooms.allSatisfy { $0.deRegCardCompleted == true }
        let leadBookerModel = PreStayLeadBookerModel(reservation: reservation)

        return PreStayInputParams(
            hotel: hotel,
            flow: .myBookings,
            stay: stay,
            hotelAddress: hotel.address?.description,
            hotelImage: hotel.images.first?.image,
            hotelName: hotel.name,
            hotelCode: reservation.hotelCode,
            hotelBrand: hotel.brand,
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
            ciolPaymentActions: paymentActions,
            isBusinessTrip: reservation.businessTrip,
            hasDERegCard: hasDERegCard,
            rateCode: reservation.rate?.code,
            rateDescription: reservation.rate?.description,
            isDirect: reservation.isDirect
        )
    }

    func updateCiolStatus(forStay stay: Stay?, to ciolStatus: CiolStatus) {
        guard let stay else { return }

        let payload = UpdateCiolStatusPayload(
            reservationIds: stay.roomIds,
            hotelId: stay.hotelCode,
            ciolStatus: ciolStatus
        )

        dataProvider.updateCiolStatus(payload: payload) { [weak self] response, error in
            self?.analyticsManager.trackUpdateCiolStatus(
                screen: PIAnalytics.StateNames.myBookings,
                payload: payload,
                error: error,
                response: response
            )
        }
    }
}

extension ReservationsInteractor: Trackable {
    var screenName: String { "" }
    var trackScreen: Bool { false }
    var environment: String { AnalyticsConstants.environment }
    var loggedIn: LoggedInAnalytic { UserSessionManager.sharedInstance.currentUser != nil ? .loggedIn : .notLoggedIn }
    var timeZone: String { TimeZone.current.description }
    var language: String { Locale.current.language.languageCode?.identifier ?? "n/a" }
    var screenType: String { PIAnalytics.StateTypes.myBookings }
    var customParameters: [String: Any]? { nil }

    func applicationDidTakeScreenshot() { }
}
