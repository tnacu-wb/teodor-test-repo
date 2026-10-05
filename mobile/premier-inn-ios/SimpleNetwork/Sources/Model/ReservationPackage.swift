//
//  ReservationPackage.swift
//  SimpleNetwork
//
//  Created by Santa Gurung on 21/12/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public struct ReservationPackageList: Codable {
    let reservationPackages: [ReservationPackage]

    public var hasChildFreeBreakfast: Bool {
        !reservationPackages.filter { $0.packageCode == .freeChildBreakfast }.isEmpty
    }

    public var mealDealPackages: [ReservationPackage] {
        reservationPackages.filter { $0.packageCode.isMealDeal }
    }

    public var otherPackages: [ReservationPackage] {
        reservationPackages.filter { !$0.packageCode.isMealDeal }
    }

    public var numberOfEarlyCheckInPackages: Int {
        reservationPackages.filter { $0.packageCode.isEarlyCheckIn }.count
    }

    public var numberOfLateCheckOutPackages: Int {
        reservationPackages.filter { $0.packageCode.isLateCheckOut }.count
    }
}

public extension ReservationPackageList {
    init(from decoder: Decoder) throws {
        var container = try decoder.unkeyedContainer()
        var packages = [ReservationPackage]()

        while !container.isAtEnd {
            let element = try container.decode(ReservationPackage.self)
            packages.append(element)
        }
        reservationPackages = packages
    }

    var toDictionary: [PIDictionary] {
        reservationPackages.compactMap { $0.toDictionary }
    }
}

public struct ReservationPackage: Codable {
    let packageCode: UpsellPackageCode
    let description: String
    let unitPrice: Double
    let totalQuantity: Int
    let computedPrice: Double

    public var isChildFreeBreakfast: Bool {
        packageCode == .freeChildBreakfast
    }

    var toDictionary: PIDictionary {
        [
            "packageCode": packageCode.rawValue,
            "description": description,
            "unitPrice": unitPrice,
            "totalQuantity": totalQuantity,
            "computedPrice": computedPrice
        ]
    }
}

public enum UpsellPackageCode: String, Codable {
    case mealDealDinner = "MD2DIN"
    case mealDealBreakfast = "MDBFST"
    case mealDealBeverage = "MDBEVA"
    case mealDealWhole = "MDP"
    case premierInnBreakfast = "BFADBF"
    case continentalBreakfast = "BFADCT"
    case freeChildBreakfast = "BFCHDF"
    case earlyCheckIn = "HSCKIN"
    case lateCheckOut = "HSCOU2"
    case donationOne = "ZCHRY1"
    case donationTwo = "ZCHRY2"
    case donationSeven = "ZCHRY7"
    case unknown

    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        guard let status = try? container.decode(String.self),
              let result = UpsellPackageCode(rawValue: status) else {
            self = .unknown
            return
        }

        self = result
    }

    public var isMealDeal: Bool {
        switch self {
        case .mealDealWhole, .mealDealDinner, .mealDealBreakfast, .mealDealBeverage:
            return true
        default:
            return false
        }
    }

    var isEarlyCheckIn: Bool {
        self == .earlyCheckIn
    }

    var isLateCheckOut: Bool {
        self == .lateCheckOut
    }

    public var isDonation: Bool {
        switch self {
        case .donationOne, .donationTwo, .donationSeven:
            return true
        default:
            return false
        }
    }
}
