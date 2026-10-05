//
//  Reservation.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 31/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation

enum ReservationError: LocalizedError {
    case missingReservationDetails
    case missingReservationConfirmationNumber
    case missingLastName
    case missingReservationIdentifier
    case missingSessionId
    case missingBookerDictionary
    case missingUserIdentifier
    case missingToken
    case missingArrivalDate

    var errorDescription: String? { String(describing: self)	}
}

public struct Reservation {
    // MARK: - Properties

    let arrivalDateString: String
    let checkOutDateString: String
    public let confirmationNumber: String
    public let hotelCode: String
    public var nights: Int {
        guard let startDate = arrivalDate else { return 0 }

        return startDate.numberOfNights(to: checkOutDate)
    }
    public let rate: Rate?
    public var rooms: [Room]
    public var booker: User?
    let address: Address?
    public let totalCost: Cost?
    public let cityTax: Cost?
    public let prepaid: Bool
    public let prepaidAmount: Cost?
    let promotionText: String?
    let bookingType: String?
    public let cancelable: Bool
    public let cancelableText: String?
    public let amendable: Bool
    let amendableText: String?
    let amendOnline: Bool
    let amendOnlineText: String?
    public let checkInOnline: Bool?
    public let isCheckInOnlineAvailable: Bool?
    public let isCheckOutOnlineAvailable: Bool?
    let checkInText: String?
    let checkInDate: String
    let smsConfirmationRequired: Bool
    let emailConfirmationSent: Bool
    public let changeCard: Bool
    public let businessTrip: Bool
    public let cancelled: Bool
    public let sessionId: String?
    public var arrivalDate: Date? { DateFormatter.parameterFormatter.date(from: arrivalDateString) }
    public var checkOutDate: Date? { DateFormatter.parameterFormatter.date(from: checkOutDateString) }
    public var adultsCount: Int { rooms.map { $0.adults }.reduce(0, +) }
    public var childrenCount: Int { rooms.map { $0.children }.reduce(0, +) }
    public var guestsCount: Int { rooms.map { $0.children + $0.adults }.reduce(0, +) }
    public var roomsTotalCost: Cost {
        guard let currency = rooms.first?.totalCost?.currencyCode else { return Cost.zeroPounds }

        return rooms.reduce(Cost(amount: 0, currencyCode: currency)) { (result, room) -> Cost in
            guard let cost = room.totalCost else { return result }
            guard let sum = result + cost else { return result }

            return sum
        }
    }
    public var token: String?
    public var bookingFlowId: String?
    public var operaBasketReference: String?
    public var basketStatus: BasketStatus?
    public var balanceOutstanding: Cost?
    public var isDigitalKey: Bool?
    public var digitalKeyIdentifier: String?
    public let isDirect: Bool
    public var upsellsAddOnEnabled: Bool?
    public var paymentOption: String?

    public var mealTotalCost: Cost? {
        guard let currency = foodUpsells.first?.price.currencyCode else { return Cost.zeroPounds }

        return foodUpsells.reduce(Cost(amount: 0, currencyCode: currency)) { (result, upsellItem) -> Cost in
            guard let sum = result + upsellItem.price else { return result }

            return sum
        }
    }

    // Amend
    public var amendRestrictions: AmendRestrictions?

    // Upsells

    /// Upsells items chosen by the user
    public var upsellItems: [UpsellItem] {
        didSet {
            let sorted = upsellItems.sorted(by: { $0.order ?? 0 < $1.order ?? 1 })
            if sorted != upsellItems {
                upsellItems = sorted
            }
        }
    }
    /// Food upsell items chosen by the user from `upsellItems` array
    public var foodUpsells: [UpsellItem] {
        upsellItems.filter {
            $0.foodUpsell == true || $0.upsellOperaId == .freeChildBreakfast
        }
    }
    /// Extra upsell items chosen by the user from `upsellItems` array
    public var extraUpsells: [UpsellItem] {
        upsellItems.filter { upsellItem in
            upsellItem.isExtraUpsell &&
            UpsellItemOperaId.extras.contains(where: { item in
                item.rawValue == upsellItem.id
            })
        }
    }
    /// All available extra upsells i.e. eci and lco (excludes the wifis)
    public var availableExtraUpsells: [UpsellItem]? {
        availableUpsells?.filter { upsellItem in
            upsellItem.isExtraUpsell &&
            UpsellItemOperaId.extras.contains(where: { item in
                item.rawValue == upsellItem.id
            })
        }
    }

    /// Wi-Fi upsell items chosen by the user from `upsellItems` array
    public var wifiUpsells: [UpsellItem] {
        // We are not sure if all codes are food upsells or other upsells like card fee
        upsellItems.filter { $0.upsellOperaId == .ultimateWifi24Hours }
    }

    /// Upsells available items
    public var availableUpsells: [UpsellItem]?
    /// Food upsell items from `availableUpsells` array
    public var availableFoodUpsells: [UpsellItem]? {
        // We are not sure if all codes are food upsells or other upsells like card fee

        guard let upsellItemsAllowed = UserSessionManager.sharedInstance.currentUser?.company?.bookingAllowances?
              .upsellItemsAllowed else {
            return availableUpsells?.filter {
                $0.foodUpsell &&
                $0.upsellOperaId != .freeChildBreakfast
            }
        }

        return availableUpsells?.filter { $0.foodUpsell && upsellItemsAllowed.contains(String($0.code ?? 0)) }
    }
    /// Wi-Fi upsell items from `availableUpsells` array
    public var availableWifiUpsells: [UpsellItem]? {
        // We are not sure if all codes are food upsells or other upsells like card fee

        let wifiUpsells = availableUpsells?.filter { $0.upsellOperaId == .ultimateWifi24Hours }

        guard let company = UserSessionManager.sharedInstance.currentUser?.company else { return wifiUpsells ?? [] }
        guard let allowedCompanyUpsellsCodes = (company.bookingAllowances?.upsellItemsAllowed?.compactMap { Int($0) })
            else { return [] }

        return wifiUpsells?.filter { allowedCompanyUpsellsCodes.contains($0.code ?? 0) } ?? []
    }

    public var breakfasts: [UpsellItem]

    public var reservationPackageList: ReservationPackageList?

    public var preferences: [ReservationPreference]?

    public var selectedDonation: Cost? {
        // To work out if the user has a selected donation we have to check the package list, this is using a hardcode for now.
        guard let reservationPackages = reservationPackageList?.reservationPackages else { return nil }

        if let package = reservationPackages.first(where: { $0.packageCode.isDonation }) {
            return Cost(amount: package.computedPrice, currencyCode: totalCost?.currencyCode ?? "GBP")
        }

        return nil
    }
    // MARK: - Init

    public init(dictionary: PIDictionary, manageBookingOperaToken: String? = nil) throws {
        self.sessionId = dictionary["sessionId"] as? String
        self.availableUpsells = Reservation.getAvailableUpsells(dictionary: dictionary)

        guard let dictionary = dictionary["reservationDetails"] as? PIDictionary
            else { throw ReservationError.missingReservationDetails }
        guard let confirmationNumber = dictionary["confirmationNumber"] as? String
            else { throw ReservationError.missingReservationConfirmationNumber }

        let isDirect = !(dictionary["isThirdPartyBooking"] as? Bool ?? false)

        let bookerDictionary = dictionary["booker"] as? PIDictionary
        guard !isDirect || bookerDictionary != nil else { throw ReservationError.missingBookerDictionary }

        let rooms = Reservation.getRooms(dictionary: dictionary)

        self.confirmationNumber = confirmationNumber
        self.hotelCode = dictionary["hotelCode"] as? String ?? ""
        self.arrivalDateString = dictionary["arrivalDate"] as? String ?? ""
        self.checkOutDateString = dictionary["departureDate"] as? String ?? ""
        self.rate = Reservation.getRate(dictionary: dictionary)
        self.rooms = rooms
        self.cancelled = rooms.contains(where: { $0.bookingStatus == .cancelled })
        self.booker = try? User(dictionary: ["contactDetail": bookerDictionary as Any], sessionId: nil)
        self.address = try? Address(dictionary: dictionary["address"] as? PIDictionary)
        self.cityTax = try? Cost(dictionary: dictionary["cityTax"] as? PIDictionary)
        self.totalCost = Reservation.getTotalCost(cancelled: cancelled, dictionary: dictionary["totalCost"] as? PIDictionary)
        self.prepaid = dictionary["prepaid"] as? Bool ?? false
        self.prepaidAmount = try? Cost(dictionary: dictionary["prepaidAmount"] as? PIDictionary)
        self.promotionText = dictionary["promotionText"] as? String ?? ""
        self.bookingType = dictionary["bookingType"] as? String ?? ""
        self.cancelable = dictionary["cancelable"] as? Bool ?? false
        self.cancelableText = dictionary["cancelableText"] as? String ?? ""
        self.amendable = dictionary["amendable"] as? Bool ?? false
        self.amendableText = dictionary["amendableText"] as? String ?? ""
        self.amendOnline = dictionary["amendOnline"] as? Bool ?? false
        self.amendOnlineText = dictionary["amendOnlineText"] as? String ?? ""
        self.checkInOnline = dictionary["checkInOnline"] as? Bool ?? false
        self.isCheckInOnlineAvailable = dictionary["checkInOnlineAvailable"] as? Bool ?? false
        self.isCheckOutOnlineAvailable = dictionary["isCheckOutOnlineAvailable"] as? Bool ?? false
        self.checkInText = dictionary["checkInText"] as? String ?? ""
        self.checkInDate = dictionary["checkInDate"] as? String ?? ""
        self.smsConfirmationRequired = dictionary["smsConfirmationRequired"] as? Bool ?? false
        self.emailConfirmationSent = dictionary["emailConfirmationSent"] as? Bool ?? false
        self.changeCard = dictionary["changeCard"] as? Bool ?? false
        self.businessTrip = dictionary["business"] as? Bool ?? false
        self.upsellItems = Reservation.getUpsellItems(dictionary: dictionary)
        self.breakfasts = Reservation.getBreakfasts(dictionary: dictionary)
        self.token = manageBookingOperaToken
        self.bookingFlowId = dictionary["bookingFlowId"] as? String
        self.operaBasketReference = dictionary["operaBasketReference"] as? String
        if let basketStatusString = dictionary["basketStatus"] as? String,
           let basketStatus = BasketStatus(rawValue: basketStatusString) {
            self.basketStatus = basketStatus
        }
        self.balanceOutstanding = try? Cost(dictionary: dictionary["balanceOutstanding"] as? PIDictionary)
        self.amendRestrictions = Reservation.getAmendRestrictions(dictionary: dictionary)
        self.reservationPackageList = Reservation.getPackageList(dictionary: dictionary)
        self.preferences = Reservation.getPreferencesList(dictionary: dictionary)
        self.isDigitalKey = dictionary["isDigitalKey"] as? Bool
        self.digitalKeyIdentifier = dictionary["digitalKeyIdentifier"] as? String
        self.isDirect = isDirect
        self.upsellsAddOnEnabled = dictionary["upsellsAddOnEnabled"] as? Bool
        self.paymentOption = dictionary["paymentOption"] as? String
    }
}

private extension Reservation {
    static func getRooms(dictionary: PIDictionary) -> [Room] {
        guard let rooms = dictionary["rooms"] as? [PIDictionary] else {
            return [Room]()
        }

        let dictionaries = rooms.map { dict -> PIDictionary in
            var newDict = dict
            let roomId = newDict["roomId"] as? String

            if let roomBreakdowns = dictionary["roomBreakdown"] as? [PIDictionary] {
                if let roomBreakdownDict = roomBreakdowns.first(where: { (dict) -> Bool in
                    dict["roomId"] as? String == roomId
                }) {
                    newDict["totalCost"] = roomBreakdownDict["totalRoomCost"] as? PIDictionary
                }
            }

            return newDict
        }

        return dictionaries.map { Room(dictionary: $0) }
    }

    static func getRate(dictionary: PIDictionary) -> Rate? {
        guard let plan = dictionary["ratePlan"] as? String else {
            return nil
        }

        guard let classification = dictionary["rateClass"] as? String else {
            return nil
        }

        var cost: Cost {
            guard let cost = try? Cost(dictionary: dictionary["totalCost"] as? PIDictionary) else {
                return Cost.zeroPounds
            }
            return cost
        }

        return Rate(
            plan: plan,
            classification: classification,
            description: dictionary["rateDescription"] as? String,
            text: dictionary["rateText"] as? String,
            cost: cost
        )
    }

    static func getUpsellItems(dictionary: PIDictionary) -> [UpsellItem] {
        guard let breakdown = dictionary["upsellBreakdown"] as? PIDictionary else {
            return []
        }
        guard let dictionaries = breakdown["upsellItems"] as? [PIDictionary] else {
            return []
        }

        return dictionaries.compactMap { try? UpsellItem(dictionary: $0) }
    }

    static func getBreakfasts(dictionary: PIDictionary) -> [UpsellItem] {
        guard let breakfastsDictionaries = dictionary["breakfasts"] as? [PIDictionary] else {
            return []
        }

        return breakfastsDictionaries.compactMap { try? UpsellItem(dictionary: $0) }
    }

    static func getAvailableUpsells(dictionary: PIDictionary) -> [UpsellItem]? {
        guard let availableUpsells = dictionary["upsellItemsAvailable"] as? [PIDictionary] else {
            return nil
        }

        return availableUpsells.compactMap { try? UpsellItem(dictionary: $0) }
    }

    static func getTotalCost(cancelled: Bool, dictionary: PIDictionary?) -> Cost? {
        try? Cost(dictionary: dictionary)
    }

    static func getAmendRestrictions(dictionary: PIDictionary) -> AmendRestrictions? {
        guard let amendRestrictionsDict = dictionary["amendRestrictions"] as? PIDictionary else {
            return nil
        }

        guard let data = try? JSONSerialization.data(
            withJSONObject: amendRestrictionsDict,
            options: .prettyPrinted
        ) else {
            return nil
        }

        do {
            let decoder = JSONDecoder()
            return try decoder.decode(AmendRestrictions.self, from: data)
        } catch {
            printDev(error)
            return nil
        }
    }

    static func getPackageList(dictionary: PIDictionary) -> ReservationPackageList? {
        guard let packageListDict = dictionary["reservationPackageList"] as? [PIDictionary] else {
            return nil
        }

        guard let data = try? JSONSerialization.data(
            withJSONObject: packageListDict,
            options: .prettyPrinted
        ) else {
            return nil
        }

        do {
            let decoder = JSONDecoder()
            return try decoder.decode(ReservationPackageList.self, from: data)
        } catch {
            printDev(error)
            return nil
        }
    }

    static func getPreferencesList(dictionary: PIDictionary) -> [ReservationPreference]? {
        guard let packageListDict = dictionary["preferences"] as? [PIDictionary] else {
            return nil
        }

        guard let data = try? JSONSerialization.data(
            withJSONObject: packageListDict,
            options: .prettyPrinted
        ) else {
            return nil
        }

        do {
            let decoder = JSONDecoder()
            return try decoder.decode([ReservationPreference].self, from: data)
        } catch {
            printDev(error)
            return nil
        }
    }
}

public struct AmendRestrictions: Codable {
    // true for restricted
    public let dates: Bool
    public let nights: Bool
    public let upsells: Bool
    public let addRoom: Bool
    public let editRoom: Bool
    public let editGuestNames: Bool
    public let removeRoom: Bool

    public static let siteWidePromotionsAmendRestrictions: AmendRestrictions = AmendRestrictions(
        dates: false,
        nights: false,
        upsells: false,
        addRoom: true,
        editRoom: true,
        editGuestNames: false,
        removeRoom: false
    )
}
