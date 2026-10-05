//
//  KeyInteractor.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 18/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import Foundation
import SimpleNetwork

protocol KeyInteractorDataProviderProtocol: AnyObject {
    func digitalKeyCheckIn(
        reservationId: String,
        hotelCode: String,
        completion: @escaping (_ response: DigitalKeyCheckInResponse?, _ error: Error?) -> Void
    )
    func findBookingSource(
        findBookingDetails: FindBookingDetails,
        completion: @escaping (FindBookingSource?, Error?) -> Void
    )
    func reservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        bookingDetails: BookingDetails?,
        completion: @escaping (_ confirmation: Reservation?, _ error: Error?) -> Void
    )
    func updateCiolStatus(
        payload: UpdateCiolStatusPayload,
        completion: @escaping (_ response: UpdateCiolStatusResponse?, _ error: Error?) -> Void
    )
}

extension RequestsManager: KeyInteractorDataProviderProtocol { }

typealias KeyInteractorAnalytics = AnalyticsType & AnalyticsUpdateCiolStatusTrackable

class KeyInteractor: KeyInteractorInputProtocol {
    // MARK: - Constants

    private enum Constants {
        static let deSite = "DE"
    }

    weak var presenter: KeyInteractorOutputProtocol?
    var stay: Stay
    var roomId: String?
    var isCheckedIn: Bool = false
    var roomNumber: String?
    var disableBackButton: Bool = false
    var howYourKeyWorks: Bool = false
    var requestManager: KeyInteractorDataProviderProtocol = RequestsManager()
    private var isNotificationGranted: Bool = false
    private let notificationManager: NotificationManaging
    private let analyticsManager: KeyInteractorAnalytics

    var keyState: KeyState {
        if isCheckedIn {
            return .checkedIn
        }

        if stay.userHasPassInWallet == false {
            return .digitalKeyReady
        }

        if stay.isBeforeCheckInTime {
            return .beforeCheckInTime
        }

        return .waitingForAllocation
    }

    private struct ViewModel: KeyDetailsViewModel {
        var keyState: KeyState
        var title: String
        var description: String
        var checkedInModel: CheckedInViewModel?
        var infoRows: [KeyInfoCellViewModel]?
        var shouldGoToUsingYourKeyInstructions: Bool
    }

    private struct CheckedInModel: CheckedInViewModel {
        var roomNumber: String?
    }

    var checkedInModel: CheckedInViewModel {
        CheckedInModel(roomNumber: roomNumber)
    }

    private struct InfoRowModel: KeyInfoCellViewModel {
        var title: String
        var description: String
        var content: CiolInformationModel?
        var instructionsModel: InstructionsViewModel?
        var tapAction: (() -> Void)?
    }

    private var infoCellRows: [InfoRowModel] {
        var rows = [
            InfoRowModel(
                title: PILocalizedString("usingKeyTitle"),
                description: PILocalizedString("usingKeyDescription"),
                instructionsModel: InstructionsViewModel(
                    type: .usingDigitalKey
                )
            ),
            InfoRowModel(
                title: PILocalizedString("gettingKeyTitle"),
                description: PILocalizedString("gettingKeyDescription"),
                instructionsModel: InstructionsViewModel(
                    type: .gettingDigitalKey(
                        isKioskAvailable: stay.qrCodeIsEnabled,
                        isDESite: isDESite()
                    ),
                )
            )
        ]

        let notificationsRow = InfoRowModel(
            title: PILocalizedString("digitalKeyNotificationTitle"),
            description: PILocalizedString("digitalKeyNotificationSubtitle"),
            instructionsModel: nil
        ) {
                self.analyticsManager.trackAction(
                    PIAnalytics.Action.digitalKeyTraySelected,
                    userInfo: [PIAnalytics.Keys.digitalKeyTrayNotification: true]
                )
                self.notificationManager.openSettings()
            }

        if !isNotificationGranted {
            rows.append(notificationsRow)
        }

        return rows
    }

    var viewModel: KeyDetailsViewModel {
        let viewModel = ViewModel(
            keyState: keyState,
            title: title,
            description: description,
            checkedInModel: checkedInModel,
            infoRows: infoCellRows,
            shouldGoToUsingYourKeyInstructions: howYourKeyWorks
        )

        // use it once and clear
        howYourKeyWorks = false

        return viewModel
    }

    init(
        stay: Stay,
        disableBackButton: Bool = false,
        howYourKeyWorks: Bool,
        notificationManager: NotificationManaging,
        analyticsManager: KeyInteractorAnalytics = AnalyticsManager.shared
    ) {
        self.stay = stay
        self.disableBackButton = disableBackButton
        self.howYourKeyWorks = howYourKeyWorks
        self.notificationManager = notificationManager
        self.analyticsManager = analyticsManager
    }

    func loadNotificationPermissionStatus() {
        notificationManager.checkNotificationPermission { [weak self] granted in
            guard let self else { return }
            isNotificationGranted = granted

            presenter?.updateViewModel(with: viewModel)
        }
    }

    var title: String {
        switch keyState {
        case .checkedIn:
            return PILocalizedString("yourRoomLabel")
        case .waitingForAllocation:
            return PILocalizedString("roomNotReadyMessageTitle")
        case .beforeCheckInTime:
            return PILocalizedString("digitalKeyIsReady")
        case .digitalKeyReady:
            return PILocalizedString("digitalKeySkipTheQueueMessaging")
        }
    }

    var description: String {
        let checkInHourLocalised = stay
            .isEarlyCheckInForAllRooms ? PILocalizedString("hotelDetailsEarlyCheckInTime") :
            PILocalizedString("hotelDetailsCheckInTime")

        switch keyState {
        case .checkedIn:
            return checkedInModel.roomNumber ?? ""
        case .waitingForAllocation:
            return PILocalizedString("roomNotReadyMessageDescription")
        case .digitalKeyReady, .beforeCheckInTime:
            guard let arrivalDate = stay.arrivalDate
                else { return "\(PILocalizedString("roomWillBeReadyMessageBase")) \(checkInHourLocalised)" }

            if arrivalDate.isToday {
                return "\(PILocalizedString("roomWillBeReadyMessageBase")) \(checkInHourLocalised)"
            } else {
                return "\(PILocalizedString("roomWillBeReadyMessageBase")) \(checkInHourLocalised) \(arrivalDate.localizedShortStringFormat)"
            }
        }
    }

    func allocateAndCheckInBooking(completion: @escaping (Bool?) -> Void) {
        guard isCheckedIn == false else {
            completion(true)
            return
        }

        guard let roomId = roomId else {
            completion(nil)
            return
        }

        requestManager.digitalKeyCheckIn(reservationId: roomId, hotelCode: stay.hotelCode) { response, error in
            guard error == nil,
                  let checkInStatus = response?.checkInStatus,
                  let roomNumber = response?.roomNumber else {
                self.analyticsManager.track(errorName: PIAnalytics.Error.dkCheckInFailed)
                return completion(nil)
            }

            self.isCheckedIn = checkInStatus == .success || checkInStatus == .clean
            self.roomNumber = roomNumber

            return completion(self.isCheckedIn)
        }
    }

    func trackStateAnalytics() {
        var data: PIDictionary = [:]

        data[PIAnalytics.Keys.checkInConf] = stay.arrivalDate?.analyticsDateFormat
        data[PIAnalytics.Keys.checkOutConf] = stay.checkOutDate?.analyticsDateFormat
        data[PIAnalytics.Keys.confRooms] = stay.numberOfRoooms
        data[PIAnalytics.Keys.rateCode] = stay.rateClassification
        data[PIAnalytics.Keys.bookingID] = stay.identifier
        data[PIAnalytics.Keys.confHotelCode] = stay.hotelCode
        data[PIAnalytics.Keys.roomNumber] = self.roomNumber
        data[PIAnalytics.Keys.productString] = ";\(stay.hotelCode)"
        data[PIAnalytics.Keys.customerType] = (stay.isBusinessTrip ? TripPurpose.business : TripPurpose.leisure)
            .analyticsString
        analyticsManager.trackState(self.keyState.analyticsValue, data: data)
    }

    func trackActionAnalytics(action: String) {
        var data: PIDictionary = [:]

        data[PIAnalytics.Keys.rooms] = stay.numberOfRoooms
        data[PIAnalytics.Keys.checkIn] = stay.arrivalDate?.analyticsDateFormat
        data[PIAnalytics.Keys.checkOut] = stay.checkOutDate?.analyticsDateFormat
        data[PIAnalytics.Keys.rateCode] = stay.rateClassification
        data[PIAnalytics.Keys.bookingID] = stay.identifier

        analyticsManager.trackAction(action, userInfo: data)
    }

    func setup(completion: @escaping (Bool?) -> Void) {
        guard let arrivalDate = stay.arrivalDate else { return }

        let findBookingDetails = FindBookingDetails(
            reservationId: stay.identifier,
            surname: stay.lastName,
            arrivalDate: arrivalDate,
            business: stay.isBusinessTrip
        )

        requestManager.findBookingSource(findBookingDetails: findBookingDetails) { source, error in
            guard let token = source?.token, let basketReference = source?.basketReference, error == nil else {
                self.analyticsManager.track(errorName: PIAnalytics.Error.dkFindBookingSourceFailed)
                return completion(false)
            }

            let reservationDetails = ReservationDetails(
                reservationId: basketReference,
                surname: self.stay.lastName,
                arrivalDate: arrivalDate,
                business: self.stay.isBusinessTrip,
                token: token
            )

            self.requestManager.reservation(
                reservationDetails: reservationDetails,
                hotelCode: self.stay.hotelCode,
                bookingDetails: nil
            ) { reservation, _ in
                guard let reservation, let room = reservation.rooms.first else {
                    self.analyticsManager.track(errorName: PIAnalytics.Error.dkBookingConfirmationFailed)
                    return completion(false)
                }

                self.stay.basketStatus = reservation.basketStatus
                self.stay.reservationPackageList = reservation.reservationPackageList
                self.stay.isCheckInOnlineAvailable = reservation.isCheckInOnlineAvailable
                self.roomId = room.roomId

                // If the user has the pass in the wallet and they have checked in then show them the 'checked in' state or if they add the key then this will show them their room number straight away once key has been added
                if room.bookingStatus == .checkedIn && self.stay.userHasPassInWallet {
                    self.isCheckedIn = true
                    self.roomNumber = room.roomNumber
                }
                LocalReservationManager.shared.update(with: [self.stay])

                return completion(true)
            }
        }
    }

    func instructionsModel(for type: InstructionsType) -> InstructionsViewModel? {
        let model = infoCellRows.compactMap({ $0.instructionsModel }).first(where: { $0.type == type })

        return model
    }

    func updateCiolStatus() {
        let payload = UpdateCiolStatusPayload(
            reservationIds: stay.roomIds,
            hotelId: stay.hotelCode,
            ciolStatus: .walletPass
        )

        requestManager.updateCiolStatus(payload: payload) { [weak self] response, error in
            self?.analyticsManager.trackUpdateCiolStatus(
                screen: self?.title ?? "",
                payload: payload,
                error: error,
                response: response
            )
        }
    }

    /// Returns `True` if the hotel is from `ASSA Abloy sites (DE sites)`
    private func isDESite() -> Bool {
        stay.hotelCountry?.isoCode.uppercased() == Constants.deSite
    }
}
