//
//  CheckInOnlineInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 11/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

struct CheckInOnlineModel {
    let sessionId: String
    let reservation: Reservation
    let hotel: Hotel
}

protocol CheckInOnlineInteractorProtocol {
    var viewModel: CheckInOnlineViewModel? { get }
    var checkInOnlineModel: CheckInOnlineModel? { get }
    var screenTitle: String { get }
    var termsAndConditionsUrl: URL? { get }
    var paidInFull: Bool { get }
    var reservation: Reservation? { get }
    var checkInSession: CheckInOnlineSessionResponse { get }
    var acceptedCreditCards: [CardType]? { get }

    func updated(leadGuest: LeadGuest, at index: Int)
    func checkIn(
        shouldCompleteFullCIOL: Bool,
        completion: @escaping (_ success: Bool, _ prepaid: Bool?, _ errorMessage: String?) -> Void
    )
    func dataToBeTrackedForLaunch() -> [String: Any]?
}

protocol CheckInOnlineInteractorDelegate: AnyObject {
    func finishedLoadingResources()
    func failedToLoadResources(with errorTitle: String, and errorMessage: String, and cancelActionTitle: String)
    func show(edit leadGuest: LeadGuest, at index: Int, hasAdditionalGuest: Bool, bookerAddress: Address)
}

protocol CheckInOnlineInteractorDataProvider {
    func reservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        bookingDetails: BookingDetails?,
        completion: @escaping (Reservation?, Error?) -> Void
    )
    func loadHotel(with hotelCode: String, completion: @escaping (Hotel?, Error?) -> Void)

    // CIOL
    func startCheckInOnlineSession(
        with params: StartCheckInRequestParameters,
        completion: @escaping (_ response: CheckInOnlineSessionResponse?, _ error: Error?) -> Void
    )
    func addCheckInOnlineGuestDetails(
        with params: AddCheckInOnlineGuestDetailsParameters,
        completion: @escaping ( _ success: Bool?, _ error: Error?) -> Void
    )
    func addCheckInOnlineUpsells(
        withSessionID sessionID: String,
        confirmationNumber: String,
        andUpsells upsells: [UpsellItem],
        completion: @escaping (_ response: CheckInOnlineAddUpsellsResponse?, _ error: Error?) -> Void
    )
    func closeCheckInOnlineSession(
        withSessionID sessionID: String,
        completion: @escaping ( _ success: Bool?, _ error: Error?) -> Void
    )
}

extension RequestsManager: CheckInOnlineInteractorDataProvider {
    func startCheckInOnlineSession(
        with params: StartCheckInRequestParameters,
        completion: @escaping (String?, Error?) -> Void
    ) { }
}

private struct RoomGuestModel {
    var room: Room
    var nextDestination: String?
    var useBookerAddress: Bool
}

extension RoomGuestModel: RoomAndNextDestinationModel {}

class CheckInOnlineInteractor {
    private typealias RoomDetailsAndCompletion = (roomDetails: [RoomViewModel], allValid: Bool)

    private struct ViewModel: CheckInOnlineViewModel {
        let hotelName: String
        let staySummary: String
        let rooms: [CheckInOnlineRoomViewModel]
        let canCheckIn: Bool
        let footerModel: CheckInOnlineFooterViewModel
    }

    private struct RoomViewModel: CheckInOnlineRoomViewModel {
        let roomName: String
        let guestDescription: NSAttributedString
        let guestInformationComplete: Bool
        let editGuestDidTap: () -> Void
    }

    private struct FooterViewModel: CheckInOnlineFooterViewModel {
        let termsMessage: NSAttributedString?
        let checkInDescription: NSAttributedString?
        let breakdownRows: [(title: String, value: String)]?
        let totalPrice: (title: String, value: String)?
        let ctaTitle: String
        let ctaIcon: UIImage?
        let showCardsAccepted: Bool
        let paymentCardImageUrls: [URL]?
    }

    private var lastName: String
    private var hotel: Hotel?
    private var roomDetails: [RoomGuestModel]?
    private weak var delegate: CheckInOnlineInteractorDelegate?
    private var checkInDescription: NSAttributedString {
        guard let reservation = reservation, reservation.amendable == true else { return NSAttributedString() }

        return NSAttributedString(
            string: PILocalizedString("checkInOnlineConfirmDescription", comment: ""),
            attributes: [
                NSMutableAttributedString.Key.paragraphStyle: subTextParagraphStyle,
                NSMutableAttributedString.Key.foregroundColor: UIColor.TintD1,
                NSMutableAttributedString.Key.font: UIFont.BodySmall()
            ]
        )
    }
    private var termsDescription: NSAttributedString {
        let mutableString = NSMutableAttributedString(
            string: PILocalizedString("checkInOnlineConfirmTermsDescription", comment: ""),
            attributes: [
                NSMutableAttributedString.Key.paragraphStyle: subTextParagraphStyle,
                NSMutableAttributedString.Key.foregroundColor: UIColor.TintD1,
                NSMutableAttributedString.Key.font: UIFont.BodySmall()
            ]
        )

        guard let range = mutableString.string.ranges(of: PILocalizedString(
            "checkInOnlineConfirmTermsHighlight",
            comment: ""
        )).first else { return NSAttributedString(attributedString: mutableString) }
        mutableString.addAttributes(
            [
                NSMutableAttributedString.Key.foregroundColor: UIColor.BasePurple,
                NSMutableAttributedString.Key.font: UIFont.BodySmall()
            ],
            range: range
        )

        return NSAttributedString(attributedString: mutableString)
    }
    private var sessionId: String?

    private let subTextParagraphStyle: NSParagraphStyle = {
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.alignment = .center
        paragraphStyle.lineSpacing = 6

        return paragraphStyle
    }()

    private let dataProvider: CheckInOnlineInteractorDataProvider = RequestsManager()

    var checkInSession: CheckInOnlineSessionResponse
    var reservation: Reservation? {
        didSet {
            guard let reservation = reservation else { return }
            roomDetails = reservation.rooms.map {
                if $0.leadGuest?.displayName == UserSessionManager.sharedInstance.currentUser?.displayName {
                    $0.leadGuest = UserSessionManager.sharedInstance.currentUser
                } else if $0.leadGuest?.displayName == reservation.booker?.displayName {
                    $0.leadGuest = reservation.booker
                }
                $0.leadGuest?.address = reservation.booker?.address
                return RoomGuestModel(room: $0, nextDestination: nil, useBookerAddress: true)
            }
        }
    }
    var analytics: AnalyticsType = AnalyticsManager.shared

    // MARK: - Lifecycle

    init(
        with identifier: String,
        guest lastName: String,
        and arrivalDate: Date,
        delegate: CheckInOnlineInteractorDelegate?,
        and session: CheckInOnlineSessionResponse
    ) {
        self.checkInSession = session
        self.delegate = delegate
        self.lastName = lastName
        setup(with: identifier, guest: lastName, and: arrivalDate)
    }

    private func setup(with identifier: String, guest lastName: String, and arrivalDate: Date) {
        let reservationDetails = ReservationDetails(
            reservationId: identifier,
            surname: lastName,
            arrivalDate: arrivalDate,
            business: false,
            token: nil
        )

        dataProvider
            .reservation(reservationDetails: reservationDetails, hotelCode: nil, bookingDetails: nil) { reservation, _ in
            self.reservation = reservation

            guard let reservation = self.reservation else { return self.failedToLoadResources(with: PILocalizedString(
                "Failed to get reservation",
                comment: ""
            )) }

            self.dataProvider.loadHotel(with: reservation.hotelCode, completion: { (hotel, _) in
                self.hotel = hotel
                guard self.hotel != nil else { return self.failedToLoadResources(with: PILocalizedString(
                    "Failed to get hotel information",
                    comment: ""
                )) }
                self.delegate?.finishedLoadingResources()
            })
        }
    }

    private func failedToLoadResources(with description: String) {
        delegate?.failedToLoadResources(
            with: PILocalizedString("Something went wrong", comment: ""),
            and: description,
            and: PILocalizedString("Ok", comment: "")
        )
    }

    private func roomModels(for roomDetails: [RoomGuestModel]) -> RoomDetailsAndCompletion {
        var roomModels: [RoomViewModel] = []

        for (index, roomDetail) in roomDetails.enumerated() {
            let roomName = String(format: PILocalizedString("roomNumberDetailsTitle", comment: ""), (index + 1))

            roomModels.append(
                RoomViewModel(
                    roomName: roomName,
                    guestDescription: guestDescription(for: roomDetail),
                    guestInformationComplete: guestInformationIsComplete(for: roomDetail),
                    editGuestDidTap: { [weak self] in
                        guard let guest = roomDetail.room.leadGuest?.copy() as? User else { return }
                        guard let bookerAddress = self?.reservation?.booker?.address else { return }
                        let leadGuest = LeadGuest(user: guest, nextDestination: roomDetail.nextDestination)
                        self?.trackEditRoomState(with: (index + 1))
                        self?.delegate?.show(
                            edit: leadGuest,
                            at: index,
                            hasAdditionalGuest: roomDetail.room.adults > 1,
                            bookerAddress: bookerAddress
                        )
                    }
                )
            )
        }

        let canCheckIn: Bool = roomModels.first(where: { $0.guestInformationComplete == false }) == nil

        return (roomModels, canCheckIn)
    }

    private var footerModel: FooterViewModel {
        FooterViewModel(
            termsMessage: termsDescription,
            checkInDescription: checkInDescription,
            breakdownRows: breakdownRows,
            totalPrice: priceToPayContent,
            ctaTitle: confirmButtonTitle,
            ctaIcon: confirmButtonIcon,
            showCardsAccepted: showCardsAccepted,
            paymentCardImageUrls: paymentCardImageUrls
        )
    }

    private func trackEditRoomState(with roomNumber: Int) {
        let stateType = UIApplication.topViewController()?.navigationController?.viewControllers
            .contains(where: { $0 is ReviewAndBookViewController }) ?? false ? PIAnalytics.StateTypes
            .bookingFlow : PIAnalytics.StateTypes.myBookings

        var basicProperties = analytics.analyticsProperties(stateType: stateType)
        basicProperties[PIAnalytics.Keys.checkInOnlineBookingID] = reservation?.confirmationNumber ?? "NA"

        analytics.trackState(
            String(format: PIAnalytics.StateNames.checkInOnlineGuestDetails, roomNumber),
            data: basicProperties
        )
    }

    private func guestInformationIsComplete(for roomDetail: RoomGuestModel) -> Bool {
        guard let user = roomDetail.room.leadGuest else { return false }

        if roomDetail.room.adults > 1 {
            guard user.additionalGuests?.first != nil else { return false }
        }

        if user.emailAddress.hasValue, user.contactNumber.hasValue, let country = user.country,
           country.passportRequired == false, user.address != nil {
            return true
        }

        guard let passport = user.passport, let nextDestination = roomDetail.nextDestination,
              !nextDestination.isEmpty else { return false }

        return !passport.number.isEmpty
    }

    private func guestDescription(for roomDetail: RoomGuestModel) -> NSAttributedString {
        var string = String()

        guard let user = roomDetail.room.leadGuest else { return NSAttributedString() }
        string.append(user.displayName)

        if let emailAddress = user.emailAddress { string.append("\n" + "\(emailAddress)") }
        if let address = user.address { string.append("\n\(address.line1 ?? ""), \(address.postcode ?? "")") }
        if let contactNumber = user.contactNumber { string.append("\n" + "\(contactNumber)") }
        if let country = user.country {
            string
                .append("\n" + "\(PILocalizedString("userDetailsNationalityLabel", comment: "")): " +
                "\(country.displayName)")

            if country.passportRequired == true {
                if let passportNumber = user.passport?
                   .number {
                    string.append("\n" + "\(PILocalizedString("userDetailsPassportLabel", comment: "")): " + passportNumber)
                    }
                if let nextDestination = roomDetail.nextDestination { string.append("\n" + PILocalizedString(
                    "Next destination: ",
                    comment: ""
                ) + nextDestination) }
            }
        }
        if let carRegistration = user
           .carRegistration {
            string.append("\n" + "\(PILocalizedString("userDetailsCarRegistrationLabel", comment: "")): " + carRegistration)
            }
        if roomDetail.room.adults > 1,
           let additionalGuest = user.additionalGuests?
           .first {
           string
           .append(
               "\n\(PILocalizedString("userDetailsSecondAdultLabel", comment: "")): \(additionalGuest.title) \(additionalGuest.firstName) \(additionalGuest.lastName)"
           )
           }

        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = 8

        return NSAttributedString(
            string: string,
            attributes: [
                NSAttributedString.Key.paragraphStyle: paragraphStyle,
                NSAttributedString.Key.foregroundColor: UIColor.TintD1,
                NSAttributedString.Key.font: UIFont.Body()
            ]
        )
    }

    func dataToBeTrackedOnConfirmation() -> [String: Any]? {
        var trackingDictionary📚 = [String: Any]()
        trackingDictionary📚[PIAnalytics.Keys.checkInOnlineBookingID] = self.reservation?.confirmationNumber
        trackingDictionary📚[PIAnalytics.Keys.checkInOnlinePrepaid] = true

        return trackingDictionary📚
    }

    // Here we are sending the booker's guest history number if the user checking in has the same name as the booker
    // because we don't know about anonymous bookings since the booker will have a different GHN to their account GHN,
    // thus rendering them inable to CIOL if a partial payment has been made, and they don't have booker status.
    private func extraSafeFalsePositiveGHN(for reservation: Reservation, and lastName: String) -> String? {
		guard lastName.capitalized == reservation.booker?.lastName?.capitalized else { return nil }

        return reservation.booker?.guestHistoryNumber ?? UserSessionManager.sharedInstance.currentUser?.guestHistoryNumber
    }
}

extension CheckInOnlineInteractor: CheckInOnlineInteractorProtocol {
    private struct StartCheckInRequestParams: StartCheckInRequestParameters {
        let confirmationNumber: String
        let surname: String
        let arrivalDate: Date
        let guestHistoryNumber: String?
    }

    var viewModel: CheckInOnlineViewModel? {
        guard let hotel = hotel else { return nil }
        guard let reservation = reservation else { return nil }
        guard let roomDetails = roomDetails else { return nil }

        let roomsAndCompletion = roomModels(for: roomDetails)

        return ViewModel(
            hotelName: hotel.name,
            staySummary: reservation.staySummary,
            rooms: roomsAndCompletion.roomDetails,
            canCheckIn: roomsAndCompletion.allValid,
            footerModel: footerModel
        )
    }

    var checkInOnlineModel: CheckInOnlineModel? {
        guard let hotel = self.hotel, let reservation = self.reservation,
              let sessionId = sessionId.remove() else { return nil }
        return CheckInOnlineModel(
            sessionId: sessionId,
            reservation: reservation,
            hotel: hotel
        )
    }

    var screenTitle: String {
        PILocalizedString("ciolCheckInOnlineTitle", comment: "")
    }

    var termsAndConditionsUrl: URL? {
        Constants.termsAndConditionsUrl
    }

    var paidInFull: Bool {
        guard let reservation = reservation else { return false }
        return reservation.prepaidAmount?.amount == reservation.totalCost?.amount
    }

    var acceptedCreditCards: [CardType]? {
        hotel?.acceptedCreditCards
    }

    func updated(leadGuest: LeadGuest, at index: Int) {
        guard let roomDetails = roomDetails, roomDetails.indices.contains(index) else { return }

        self.roomDetails?[index].nextDestination = leadGuest.nextDestination
        self.roomDetails?[index].room.leadGuest = leadGuest.user
    }

    func checkIn(
        shouldCompleteFullCIOL: Bool,
        completion: @escaping (_ success: Bool, _ prepaid: Bool?, _ errorMessage: String?) -> Void
    ) {
        guard let reservation = reservation else {
            completion(false, nil, ErrorMessages.CIOL.noReservation)
            return
        }

        guard let arrivalDate = reservation.arrivalDate else {
            completion(false, nil, ErrorMessages.CIOL.noReservation)
            return
        }

        cleanCIOLSession { success in
            guard success else { return completion(false, nil, ErrorMessages.CIOL.clearCIOLSession) }
            let params = self.startCheckInParams(for: reservation, and: arrivalDate)

            self.dataProvider.startCheckInOnlineSession(with: params) { response, error in
                guard let session = response else { return completion(false, nil, ErrorMessages.CIOL.noSessionId) }
                self.checkInSession = session

                guard let booker = reservation.booker else { return completion(false, nil, ErrorMessages.CIOL.noBooker) }
                guard let roomsAndNextDestinations = self.roomDetails else { return completion(
                    false,
                    nil,
                    ErrorMessages.CIOL.missingRoomsAndNextDestinations
                ) }

                let addCheckInOnlineGuestDetailsParameters = AddCheckInOnlineGuestDetailsParameters(
                    sessionID: self.checkInSession.sessionID,
                    confirmationNumber: reservation.confirmationNumber,
                    asBusinessTrip: false,
                    booker: booker,
                    roomsAndNextDestinations: roomsAndNextDestinations
                )
                    self.dataProvider
                        .addCheckInOnlineGuestDetails(with: addCheckInOnlineGuestDetailsParameters) { success, error in
                    guard success == true else { return completion(false, nil, ErrorMessages.CIOL.addGuestDetailsFailed) }
                    guard shouldCompleteFullCIOL == true else {
                        return completion(true, nil, nil)
                    }

                    // If user can add upsells, tell presenter here. Otherwise send add upsells request then proceed to payment (if required).

                    self.dataProvider.addCheckInOnlineUpsells(
                        withSessionID: self.checkInSession.sessionID,
                        confirmationNumber: reservation.confirmationNumber,
                        andUpsells: reservation.foodUpsells + reservation.wifiUpsells
                    ) { addUpsellsResponse, error in
                        guard error == nil else { return completion(false, nil, ErrorMessages.CIOL.addUpsellsFailed) }

                        if let sessionId = addUpsellsResponse?.newSessionID {
                            self.checkInSession.sessionID = sessionId
                        }

                        self.updateLocalStay(with: reservation.confirmationNumber, is: reservation.cancelable)

                        completion(true, addUpsellsResponse?.paymentRequired ?? false == false, nil)
                    }
                }
            }
        }
    }

    private func startCheckInParams(for reservation: Reservation, and arrivalDate: Date) -> StartCheckInRequestParams {
        let params = StartCheckInRequestParams(
            confirmationNumber: reservation.confirmationNumber,
            surname: self.lastName,
            arrivalDate: arrivalDate,
            guestHistoryNumber: self.extraSafeFalsePositiveGHN(for: reservation, and: self.lastName)
        )

        return params
    }

    private func updateLocalStay(with identifier: String, is cancelable: Bool) {
        let reservationsManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)

        if let stay = reservationsManager.items.first(where: { $0.identifier == identifier }) {
            self.sessionId = nil

            stay.checkedIn = true
            stay.amendable = false
            stay.cancelable = cancelable

            _ = LocalReservationManager.shared.update(with: [stay])
        }
    }

    private func cleanCIOLSession(completion: @escaping (Bool) -> Void) {
        dataProvider.closeCheckInOnlineSession(withSessionID: checkInSession.sessionID) { success, _ in
            completion(success ?? true)
        }
    }

    func dataToBeTrackedForLaunch() -> [String: Any]? {
        var trackingDictionary📚 = [String: Any]()
        trackingDictionary📚[PIAnalytics.Keys.checkInOnlineBookingID] = self.reservation?.confirmationNumber
        trackingDictionary📚[PIAnalytics.Keys.checkInOnlineHotelCode] = self.reservation?.hotelCode

        return trackingDictionary📚
    }
}

// MARK: Check In Model
extension CheckInOnlineInteractor {
    private var breakdownRows: [(String, String)]? {
        guard checkInSession.paymentRequired else { return nil }
        guard let reservation = reservation else { return nil }

        var breakdowns: [(String, String)] = [(
            PILocalizedString("bookingSummaryHotelStayLabel", comment: "Booking summary hotel stay label") + "\n(" +
                reservation.nightsCountDescription + ", " + reservation.roomsCountDescription + ")",
            reservation.roomsTotalCost.localizedValue
        )]

        let uniqueBreakfastCodes = Set(reservation.breakfasts.compactMap { $0.code })
        for code in uniqueBreakfastCodes {
            guard let breakdownContent = upsellBreakdownContent(for: code, in: reservation.breakfasts) else { continue }
            breakdowns.append(breakdownContent)
        }

        if let wifiCode = reservation.wifiUpsells.first?.code, let breakdownContent = upsellBreakdownContent(
            for: wifiCode,
            in: reservation.wifiUpsells
        ) {
            breakdowns.append(breakdownContent)
        }

        return breakdowns
    }

    private func upsellBreakdownContent(for code: Int, in upsells: [UpsellItem]) -> (String, String)? {
        guard let upsell = upsells.first(where: { $0.code == code }) else { return nil }
        let filteredUpsells = upsells.filter { $0.code == code }
        let totalAmount = filteredUpsells.compactMap { $0.price.amount.doubleValue }.reduce(0.0, +)
        var upsellLabel = upsell.legend

        if filteredUpsells.count > 1 {
            upsellLabel.append(" x \(filteredUpsells.count)")
        }

        return (upsellLabel, Cost(amount: totalAmount, currencyCode: upsell.price.currencyCode).localizedValue)
    }

    private var priceToPayContent: (String, String)? {
        guard checkInSession.paymentRequired else { return nil }
        guard let outstandingAmount = reservation?.outstandingAmount else { return nil }

        return (PILocalizedString("Total to pay"), outstandingAmount.localizedValue)
    }

    private var confirmButtonTitle: String {
        checkInSession.paymentRequired ? PILocalizedString("userDetailsSubmitButtonContinue") : PILocalizedString(
            "ciolConfirmCheckInButtonTitle",
            comment: ""
        )
    }

    private var confirmButtonIcon: UIImage? {
        checkInSession.paymentRequired ? #imageLiteral(resourceName: "padlock") : nil
    }

    private var showCardsAccepted: Bool {
        checkInSession.paymentRequired
    }

    private var paymentCardImageUrls: [URL]? {
        guard let cards = hotel?.acceptedCreditCards else { return nil }

        return (cards.sorted { $0.listOrder < $1.listOrder }).compactMap { $0.logoURL }
    }
}
