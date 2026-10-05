//
//  Stay.swift
//  PremierInn
//
//  Created by Marcello Mascia on 03/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation
import CoreLocation
import UIKit

public enum StayBookingStatus: String {
    case future = "FUTURE"
    case past = "PAST"
    case checkedIn = "CHECKED_IN"
}

public protocol DictionaryInitialisable: Equatable {
    init(dictionary: PIDictionary) throws

    var identifier: String { get }
    var dictionary: PIDictionary { get }
}

private enum StayError: LocalizedError {
    case missingHotelCode
    case missingTitle
    case missingLastName
    case missingIdentifier
    case missingArrivalDate
    case missingCheckOutDate
    case missingDepartureDate

	var errorDescription: String? { String(describing: self)	}
}

public enum ReservationImportType: String {
    case imported
    case account
    case unknown
}

public class Stay: DictionaryInitialisable {
    public let identifier: String // bookingReference
    public var operaBasketReference: String? // basketReference
    public var basketStatus: BasketStatus?
    public var stayBookingStatus: StayBookingStatus?
    public let hotelName: String
    public let hotelCode: String
    public let hotelCountry: Country?
    public let lastName: String
    public var cancelled: Bool
    public var importType: String
    public var importName: String?
    public var leadGuestName: String?
    public let isBusinessTrip: Bool
    public let cityTax: Cost?
    public let prePaidAmount: Cost?
    public let balanceOutstanding: Cost?
    public let rateClassification: String?
    public let rateText: String?
    public let checkInOnline: Bool
    public var isCheckInOnlineAvailable: Bool?
    public var isCheckOutOnlineAvailable: Bool?
    public let guestHistoryNumber: String?
	public var bookerEmail: String?
    public let hotelLatitude: CLLocationDegrees
    public let hotelLongitude: CLLocationDegrees

    public let roomIds: [String]
    public var numberOfRoooms: Int
    public var totalCost: Cost?
    public var arrivalDateString: String
    public var checkOutDateString: String
    public var arrivalDate: Date? { DateFormatter.parameterFormatter.date(from: arrivalDateString) }
    public var checkOutDate: Date? { DateFormatter.parameterFormatter.date(from: checkOutDateString) }
    public var amendable: Bool
    public var cancelable: Bool
    public var checkedIn: Bool
    public var token: String?
    public var bookingFlowId: String?
    public var reservationPackageList: ReservationPackageList?
    public var isDigitalKey: Bool?
    public var digitalKeyIdentifier: String?

    public var isDirect: Bool

    public var numberOfAccessibleRooms: Int
    public var uniqueLettingTypeNames: [String]

    // PIBA CNP fields
    public var paymentOption: String?
    public var upsellsAddOnEnabled: Bool?

    public var keyManager: PassManagerProtocol = PassManager()

    public required init(dictionary: PIDictionary) throws {
        guard let hotelCode = dictionary["hotelCode"] as? String else { throw StayError.missingHotelCode }
        guard let hotelName = dictionary["hotelName"] as? String else { throw StayError.missingTitle }
        guard let identifier = dictionary["identifier"] as? String else { throw StayError.missingIdentifier }
        guard let arrivalDate = dictionary["arrivalDate"] as? String else { throw StayError.missingArrivalDate }
        guard let checkOutDate = dictionary["checkOutDate"] as? String else { throw StayError.missingCheckOutDate }

        self.hotelCode = hotelCode
        self.hotelName = hotelName
        self.hotelCountry = Country.countriesList.first(where: { $0.isoCode == dictionary["hotelCountry"] as? String })
        self.lastName = { // "lastName"
            if let leadGuestSurname = dictionary["leadGuestSurname"] as? String {
                return leadGuestSurname
            }

            if let lastName = dictionary["lastName"] as? String {
                return lastName
            }

            return ""
        }()
        self.identifier = identifier
        self.arrivalDateString = arrivalDate
        self.checkOutDateString = checkOutDate
        self.hotelLatitude = dictionary["hotelLatitude"] as? Double ?? 0
        self.hotelLongitude = dictionary["hotelLongitude"] as? Double ?? 0
        self.cancelled = dictionary["cancelled"] as? Bool ?? false
        self.importType = dictionary["importType"] as? String ?? ReservationImportType.unknown.rawValue
        self.importName = dictionary["importName"] as? String
        self.checkInOnline = dictionary["checkInOnline"] as? Bool ?? false
        self.guestHistoryNumber = dictionary["guestHistoryNumber"] as? String
        self.amendable = dictionary["amendable"] as? Bool ?? false
        self.cancelable = dictionary["cancelable"] as? Bool ?? false
        self.leadGuestName = (dictionary["leadGuest"] as? String)?.capitalized
        self.isBusinessTrip = dictionary["business"] as? Bool ?? false
        self.totalCost = try? Cost(dictionary: dictionary["totalCost"] as? PIDictionary)
        self.cityTax = try? Cost(dictionary: dictionary["cityTax"] as? PIDictionary)
        self.rateClassification = dictionary["rateClass"] as? String
        self.rateText = dictionary["rateText"] as? String
        self.prePaidAmount = try? Cost(dictionary: dictionary["prePaidAmount"] as? PIDictionary)
        self.roomIds = dictionary["roomIds"] as? [String] ?? []
        self.numberOfRoooms = dictionary["noOfRooms"] as? Int ?? 0
        self.checkedIn = dictionary["checkedIn"] as? Bool ?? false
        self.isCheckInOnlineAvailable = dictionary["checkInOnlineAvailable"] as? Bool ?? false
        self.isCheckOutOnlineAvailable = dictionary["isCheckOutOnlineAvailable"] as? Bool ?? false
        self.token = dictionary["token"] as? String
        self.bookingFlowId = dictionary["bookingFlowId"] as? String
        self.operaBasketReference = dictionary["operaBasketReference"] as? String
        if let basketStatusString = dictionary["basketStatus"] as? String,
           let basketStatus = BasketStatus(rawValue: basketStatusString) {
            self.basketStatus = basketStatus
        }
        self.balanceOutstanding = try? Cost(dictionary: dictionary["balanceOutstanding"] as? PIDictionary)
        self.reservationPackageList = Stay.getPackageList(dictionary: dictionary)

        self.numberOfAccessibleRooms = dictionary["numberOfAccessibleRooms"] as? Int ?? 0
        self.uniqueLettingTypeNames = dictionary["uniqueLettingTypeNames"] as? [String] ?? []
        self.isDigitalKey = dictionary["isDigitalKey"] as? Bool
        self.digitalKeyIdentifier = dictionary["digitalKeyIdentifier"] as? String
        self.bookerEmail = dictionary["bookerEmail"] as? String
        self.isDirect = dictionary["isDirect"] as? Bool ?? true

        // PIBA CNP fields
        self.paymentOption = dictionary["paymentOption"] as? String
        self.upsellsAddOnEnabled = dictionary["upsellsAddOnEnabled"] as? Bool

        if let bookingStatusString = dictionary["bookingStatus"] as? String,
           let bookingStatus = StayBookingStatus(rawValue: bookingStatusString) {
            self.stayBookingStatus = bookingStatus
        }
    }

    public var dictionary: PIDictionary {
        let totalCost: [String: Any] = [
            "amount": totalCost?.amount ?? 0,
            "currency": totalCost?.currencyCode ?? ""
        ]
        let prepaidAmount: [String: Any] = [
            "amount": prePaidAmount?.amount ?? 0,
            "currency": prePaidAmount?.currencyCode ?? ""
        ]

        let balanceOutstanding: [String: Any] = [
            "amount": balanceOutstanding?.amount ?? Constants.outstandingAmountEmptyOperaCode,
            "currency": balanceOutstanding?.currencyCode ?? ""
        ]

        return [
            "hotelCode": hotelCode,
            "hotelName": hotelName,
            "hotelLatitude": hotelLatitude,
            "hotelLongitude": hotelLongitude,
            "lastName": lastName,
            "identifier": identifier,
            "arrivalDate": arrivalDateString,
            "checkOutDate": checkOutDateString,
            "importType": importType,
            "importName": importName ?? lastName,
            "cancelled": cancelled,
            "checkInOnline": checkInOnline,
            "guestHistoryNumber": guestHistoryNumber ?? "",
            "amendable": amendable,
            "cancelable": cancelable,
            "leadGuest": leadGuestName ?? "",
            "totalCost": totalCost,
            "rateClass": rateClassification ?? "",
            "rateText": rateText ?? "",
            "prePaidAmount": prepaidAmount,
            "roomIds": roomIds,
            "noOfRooms": numberOfRoooms,
            "business": isBusinessTrip,
            "checkedIn": checkedIn,
            "token": token ?? "",
            "numberOfAccessibleRooms": numberOfAccessibleRooms,
            "uniqueLettingTypeNames": uniqueLettingTypeNames,
            "bookingFlowId": bookingFlowId ?? "",
            "operaBasketReference": operaBasketReference ?? "",
            "balanceOutstanding": balanceOutstanding,
            "reservationPackageList": reservationPackageList?.toDictionary ?? [],
            "checkInOnlineAvailable": isCheckInOnlineAvailable ?? false,
            "isCheckOutOnlineAvailable": isCheckOutOnlineAvailable ?? false,
            "basketStatus": basketStatus?.rawValue ?? "",
            "hotelCountry": hotelCountry?.isoCode ?? "",
            "isDigitalKey": isDigitalKey ?? false,
            "digitalKeyIdentifier": digitalKeyIdentifier ?? "",
            "bookerEmail": bookerEmail ?? "",
            "isDirect": isDirect,
            "paymentOption": paymentOption ?? "",
            "upsellsAddOnEnabled": upsellsAddOnEnabled ?? false,
            "bookingStatus": stayBookingStatus?.rawValue ?? ""
        ]
    }

    public static func == (lhs: Stay, rhs: Stay) -> Bool {
        if (lhs.operaBasketReference == rhs.identifier) || (lhs.identifier == rhs.operaBasketReference) {
            return true
        }

        return lhs.identifier == rhs.identifier
    }

    private static func getPackageList(dictionary: PIDictionary) -> ReservationPackageList? {
        guard let packageListDict = dictionary["reservationPackageList"] as? [PIDictionary] else { return nil }
        guard let data = try? JSONSerialization.data(withJSONObject: packageListDict, options: .prettyPrinted)
            else { return nil }

        do {
            let decoder = JSONDecoder()
            return try decoder.decode(ReservationPackageList.self, from: data)
        } catch {
            printDev(error)
            return nil
        }
    }
}
