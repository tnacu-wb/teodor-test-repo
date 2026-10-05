//
//  Stay+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import PassKit

/// Payment option enum returned by backend to indicate booking payment type
/// Used to determine CIOL eligibility and payment flow
public enum BookingPaymentOption: String, Codable {
    /// Credit Card payment
    case creditCard = "CC"

    /// PIBA Card Present - NOT eligible for CIOL
    /// Guest must pay in-app during check-in
    case pibaCardPresent = "PIBA_CP"

    /// PIBA Card Not Present - Eligible for CIOL
    /// Guest pays at front desk, skips payment screen during check-in
    case pibaCardNotPresent = "PIBA_CNP"
}

extension Stay {
    /// Payment option from backend indicating booking payment type
    /// Used to determine CIOL eligibility and payment flow
    /// Backend provides this in the booking/reservation response
    var paymentOption: BookingPaymentOption? {
        if let rawValue = dictionary["paymentOption"] as? String {
            return BookingPaymentOption(rawValue: rawValue)
        }
        return nil
    }

    /// Backend flag indicating if upsells can be added during CIOL
    /// Phase 1: false for PIBA bookings
    /// Backend provides this in the booking/reservation response
    var upsellsAddOnEnabled: Bool {
        // Check if backend has provided the field
        if let value = dictionary["upsellsAddOnEnabled"] as? Bool {
            return value
        }

        // Fallback for backward compatibility: Disable upsells for PIBA bookings
        if let paymentOption = paymentOption,
           paymentOption == .pibaCardPresent || paymentOption == .pibaCardNotPresent {
            return false
        }

        // Default to true for credit card and other payment types
        return true
    }

    var datesString: String {
        var string = ""

        if let arrivalDate = arrivalDate {
            string += DateFormatter.veryShortStringFormatter.string(from: arrivalDate)
        }

        string += " - "

        if let checkOutDate = checkOutDate {
            string += DateFormatter.veryShortStringFormatter.string(from: checkOutDate)
        }

        return string
    }

    var attributedDatesString: NSAttributedString {
        let string = NSMutableAttributedString()

        if let arrivalDate = arrivalDate {
            let attributedArrivalDate = NSAttributedString(
                string: DateFormatter.veryShortStringFormatter.string(from: arrivalDate) + " ",
                attributes: nil
            )
            string.append(attributedArrivalDate)
        }

        if let checkOutDate = checkOutDate {
            let attributedCheckOutDate = NSAttributedString(
                string: "- " + DateFormatter.veryShortStringFormatter.string(from: checkOutDate),
                attributes: nil
            )
            string.append(attributedCheckOutDate)
        }

        return NSAttributedString(attributedString: string)
    }

    public var numberOfNights: Int {
        guard let startDate = arrivalDate else { return 0 }

        return startDate.numberOfNights(to: checkOutDate)
    }

    var nightsCountDescription: String {
        String.localizedStringWithFormat(
            PILocalizedString("%d night(s)", comment: "Message shown for number of nights"),
            numberOfNights
        )
    }

    var roomsCountDescription: String {
        if uniqueLettingTypeNames.count == 1 {
            return String.init(
                format: "%d %@ %@",
                numberOfRoooms,
                uniqueLettingTypeNames.first ?? "",
                String.localizedStringWithFormat(
                    PILocalizedString("room(s)", comment: "Message shown for number of rooms"),
                    numberOfRoooms
                )
            )
        } else {
            return String.localizedStringWithFormat(
                PILocalizedString("%d room(s)", comment: "Message shown for number of rooms"),
                numberOfRoooms
            )
        }
    }

    static func dictionary(
        reservation: Reservation,
        hotel: Hotel?,
        importType: ReservationImportType,
        importName: String? = nil
    ) -> PIDictionary {
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = reservation.hotelCode
        dictionary["hotelName"] = hotel?.name
        dictionary["hotelCountry"] = hotel?.address?.country?.isoCode
        dictionary["hotelLongitude"] = hotel?.coordinate.longitude
        dictionary["hotelLatitude"] = hotel?.coordinate.latitude
        dictionary["lastName"] = reservation.booker?.lastName
        dictionary["bookerEmail"] = reservation.booker?.emailAddress
        dictionary["identifier"] = reservation.confirmationNumber
        dictionary["arrivalDate"] = reservation.arrivalDate?.parameterString
        dictionary["checkOutDate"] = reservation.checkOutDate?.parameterString
        dictionary["importType"] = importType.rawValue
        dictionary["cancelled"] = reservation.cancelled
        dictionary["checkInOnline"] = reservation.checkInOnline ?? false
        dictionary["amendable"] = reservation.amendable
        dictionary["isDigitalKey"] = reservation.isDigitalKey
        dictionary["cancelable"] = reservation.cancelable
        dictionary["leadGuest"] = reservation.rooms.first?.leadGuest?.displayName
        dictionary["leadGuestSurname"] = reservation.rooms.first?.leadGuest?.lastName
        dictionary["roomIds"] = reservation.rooms.compactMap { $0.roomId }
        dictionary["noOfRooms"] = reservation.rooms.count
        dictionary["business"] = reservation.businessTrip
        dictionary["totalCost"] = reservation.totalCost?.toDictionary
        dictionary["cityTax"] = reservation.cityTax?.toDictionary
        dictionary["prePaidAmount"] = reservation.prepaidAmount?.toDictionary
        dictionary["checkedIn"] = reservation.rooms.first(where: { $0.bookingStatus == .checkedIn }) != nil
        dictionary["numberOfAccessibleRooms"] = reservation.rooms.filter({ $0.type == .accessible }).count
        dictionary["uniqueLettingTypeNames"] = Array(Set(reservation.rooms.compactMap { $0.lettingType }))
            .compactMap { LettingType(rawValue: $0)?.categoryName }
        dictionary["importName"] = importName
        dictionary["rateClass"] = reservation.rate?.classification ?? ""
        dictionary["rateText"] = reservation.rate?.text ?? ""
        dictionary["guestHistoryNumber"] = reservation.booker?.guestHistoryNumber ?? ""
        dictionary["token"] = reservation.token
        dictionary["bookingFlowId"] = reservation.bookingFlowId
        dictionary["operaBasketReference"] = reservation.operaBasketReference
        dictionary["balanceOutstanding"] = reservation.balanceOutstanding?.toDictionary
        dictionary["reservationPackageList"] = reservation.reservationPackageList?.toDictionary
        dictionary["checkInOnlineAvailable"] = reservation.isCheckInOnlineAvailable
        dictionary["isCheckOutOnlineAvailable"] = reservation.isCheckOutOnlineAvailable
        dictionary["basketStatus"] = reservation.basketStatus?.rawValue ?? ""
        dictionary["digitalKeyIdentifier"] = reservation.digitalKeyIdentifier
        dictionary["isDirect"] = reservation.isDirect
        dictionary["upsellsAddOnEnabled"] = reservation.upsellsAddOnEnabled
        dictionary["paymentOption"] = reservation.paymentOption

        return dictionary
    }

    static func dictionary(bookingDetails: BookingDetails, confirmation: BookingConfirmation) -> PIDictionary {
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = bookingDetails.hotel?.code
        dictionary["hotelName"] = bookingDetails.hotel?.name
        dictionary["hotelCountry"] = bookingDetails.hotel?.address?.country?.isoCode
        dictionary["hotelLatitude"] = bookingDetails.hotel?.coordinate.latitude
        dictionary["hotelLongitude"] = bookingDetails.hotel?.coordinate.longitude
        dictionary["lastName"] = bookingDetails.booker?.lastName
        dictionary["bookerEmail"] = bookingDetails.booker?.emailAddress
        dictionary["identifier"] = bookingDetails.operaBookingReference // bookingReference
        dictionary["arrivalDate"] = bookingDetails.criteria.arrivalDate.parameterString
        dictionary["checkOutDate"] = bookingDetails.criteria.checkOutDate?.parameterString
        dictionary["importType"] = ReservationImportType.imported.rawValue
        dictionary["cancelled"] = false
        dictionary["leadGuest"] = bookingDetails.booker?.displayName
        dictionary["roomIds"] = bookingDetails.criteria.rooms.compactMap { $0.roomId }
        dictionary["noOfRooms"] = bookingDetails.criteria.rooms.count
        dictionary["business"] = bookingDetails.bookingMode == .business
        dictionary["totalCost"] = bookingDetails.totalCost.toDictionary
        dictionary["prePaidAmount"] = bookingDetails.paymentOption == .now ? bookingDetails.totalCost.toDictionary : nil
        dictionary["numberOfAccessibleRooms"] = bookingDetails.criteria.rooms.filter({ $0.type == .accessible }).count
        dictionary["uniqueLettingTypeNames"] = [bookingDetails.rate?.lettingTypes]
        dictionary["rateClass"] = bookingDetails.rate?.classification ?? ""
        dictionary["rateText"] = bookingDetails.rate?.name ?? ""
        dictionary["token"] = nil
        dictionary["bookingFlowId"] = nil
        dictionary["operaBasketReference"] = confirmation.confirmationNumber // basketReference
        dictionary["balanceOutstanding"] = nil

        return dictionary
    }
}

extension Stay: GermanCityTaxViewModelValues {
    var cityTaxRequired: Bool {
        guard let cityTaxAmount = cityTax else { return false }

        return cityTaxAmount.amount.doubleValue > 0
    }
}

extension Stay {
    var isActive: Bool {
        guard cancelled == false else { return false }
        guard let checkOutDate = self.checkOutDate else { return false }

        return checkOutDate >= Date() || checkOutDate.isToday
    }

    var isUpcoming: Bool {
        guard cancelled == false else { return false }
        guard let arrivalDate = self.arrivalDate else { return false }

        return arrivalDate >= Date() || arrivalDate.isToday
    }

    var isPast: Bool {
        if isActive || cancelled { return false }
        guard let checkOutDate = self.checkOutDate else { return false }

        return checkOutDate < Date()
    }

    var isBeforeArrivalDate: Bool {
        guard cancelled == false else { return false }
        guard let arrivalDate = self.arrivalDate else { return false }

        return arrivalDate > Date() && arrivalDate.isToday == false
    }

    public var qrCodeEnabledAndWithin48Hours: Bool {
        guard self.arrivalDate?.isLessThan48HoursFromNow == true && isUpcoming else { return false }
        guard self.basketStatus != .preCheckedOut else { return false }
        return qrCodeIsEnabled
    }

    public var qrCodeIsEnabled: Bool {
        guard let supportedHotels = SettingsManager.sharedInstance.supportedKioskHotel else { return false }
        return supportedHotels.contains(where: { $0.hotelCode == hotelCode })
    }

    public var showHotelWifiOption: Bool {
        guard let supportedHotels = SettingsManager.sharedInstance.snpWifiEnabledHotels else { return false }
        guard supportedHotels.contains(where: { $0.hotelCode == hotelCode }) else { return false }
        guard let arrivalDate = self.arrivalDate else { return false }
        return isActive && (!isUpcoming || arrivalDate.isToday)
    }

    public var hotelFreeSSID: String {
        PILocalizedString("Premier Inn Free-WiFi")
    }

    public var hotelPaidSSID: String {
        PILocalizedString("Premier Inn Ultimate-WiFi")
    }
}

extension Stay: @retroactive Hashable {
    public func hash(into hasher: inout Hasher) {
        hasher.combine(identifier)
    }
}

extension Array where Element == Stay {
    var activeStays: [Stay] {
        self.filter { $0.isActive }.sorted { customSorting(lhs: $0, rhs: $1) }
    }

    var upcomingStays: [Stay] {
        self.filter { $0.isUpcoming }.sorted { customSorting(lhs: $0, rhs: $1) }
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


// MARK: PassKit Pass Serial Number Format
extension Stay {
    var pkPassSerialNumber: String {
        "1111\(identifier)"
    }
}

public enum ExtraCheckInOutPackageState {
    case none
    case mixed
    case validForAllRooms
}

extension Stay {
    private var earlyCheckInState: ExtraCheckInOutPackageState {
        let roomsWithEarlyCheckIn = reservationPackageList?.numberOfEarlyCheckInPackages ?? 0

        return checkInOutState(roomsWithPackage: roomsWithEarlyCheckIn)
    }

    private var lateCheckOutState: ExtraCheckInOutPackageState {
        let roomsWithLateCheckOut = reservationPackageList?.numberOfLateCheckOutPackages ?? 0

        return checkInOutState(roomsWithPackage: roomsWithLateCheckOut)
    }

    private func checkInOutState(roomsWithPackage: Int) -> ExtraCheckInOutPackageState {
        switch roomsWithPackage {
        case 0:
            return .none
        case numberOfRoooms:
            return .validForAllRooms
        case _ where roomsWithPackage < numberOfRoooms:
            return .mixed
        default:
            printDev("do we have more than one for a room?")
            return .mixed
        }
    }

    public func checkInTimeText(hotelBrand: HotelBrand) -> String {
        if hotelBrand == .premierInnGermany {
            return PILocalizedString("hotelDetailsGermanCheckInTime")
        } else {
            return PILocalizedString("hotelDetailsCheckInTime")
        }
    }

    public func checkOutTimeText(hotelBrand: HotelBrand) -> String {
        if hotelBrand == .premierInnGermany {
            return PILocalizedString("hotelDetailsGermanCheckOutTime")
        } else {
            return PILocalizedString("hotelDetailsCheckOutTime")
        }
    }

    public var shouldShowCheckInOutExtrasInfo: Bool {
        let statesWithExtra = [
            ExtraCheckInOutPackageState.mixed,
            ExtraCheckInOutPackageState.validForAllRooms
        ]

        return statesWithExtra.contains(earlyCheckInState) || statesWithExtra.contains(lateCheckOutState)
    }

    public var isEarlyCheckInForAllRooms: Bool {
        let statesWithExtra = [
            ExtraCheckInOutPackageState.mixed,
            ExtraCheckInOutPackageState.validForAllRooms
        ]

        return statesWithExtra.contains(earlyCheckInState)
    }

    public var checkInTimeHasPassed: Bool {
        guard arrivalDate?.isToday == true else { return false }

        let currentTime: () -> Date = { Date() }

        let checkInHour = isEarlyCheckInForAllRooms ? 11 : 15
        guard let checkInTime = Calendar.current.date(bySettingHour: checkInHour, minute: 0, second: 0, of: currentTime())
            else { return false }
        return currentTime() > checkInTime
    }

    public var isPreCheckedIn: Bool {
        // Check if the basketStatus is PRE_CHECKED_IN or bookingStatus is CHECKED_IN to tell a booking has been checked in (either from app or front desk)
        (basketStatus == .preCheckedIn || stayBookingStatus == .checkedIn) && !isPast
    }

    public var userHasPassInWallet: Bool {
        guard let keyId = self.digitalKeyIdentifier else { return false }
        return self.keyManager.isKeyInUsersWallet(passId: keyId)
    }

    public var roomIsReadyForAllocation: Bool {
        userHasPassInWallet && self.isUpcoming && self.isPreCheckedIn && SettingsManager.sharedInstance.featureDigitalKeys
    }

    public var isBeforeCheckInTime: Bool {
        guard let arrivalDate else { return true }

        if Date() < arrivalDate && arrivalDate.isToday == false {
            return true
        }

        return checkInTimeHasPassed == false
    }

    public var isDigitalKeyEnabled: Bool {
        self.isDigitalKey == true && UIDevice.current.userInterfaceIdiom == .phone && SettingsManager.sharedInstance
            .featureDigitalKeys
    }

    var showAddDigitalKeyButton: Bool {
        isPreCheckedIn && isDigitalKeyEnabled && !userHasPassInWallet
    }
}
