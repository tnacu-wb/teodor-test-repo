//
//  BusinessAccount.swift
//  PremierInn
//
//  Created by Freddie Parks on 11/09/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation

public enum BusinessAccountError: Error {
    case emptyDictionary
}

public struct BusinessAccount {
    public var alcoholAllowed: Bool?
    public var atosPassword: String?
    public var atosUsername: String?
    public var breakfastCode: String?
    public var carParkingAllowed: Bool?
    public var cardNotPresentAuth: Bool?
    public var customerReference: String?
    public var dinnerAllowance: String?
    public var otherChargesAllowed: Bool?
    public var purchaseOrder: String?
    public var wifiAccessAllowed: Bool?
}

public extension BusinessAccount {
    init(dictionary: PIDictionary?) throws {
        guard let dictionary = dictionary else { throw BusinessAccountError.emptyDictionary }

        self.alcoholAllowed = dictionary["alcoholAllowed"] as? Bool
        self.atosPassword = dictionary["atosPassword"] as? String
        self.atosUsername = dictionary["atosUsername"] as? String
        self.breakfastCode = dictionary["breakfastCode"] as? String
        self.carParkingAllowed = dictionary["carParkingAllowed"] as? Bool
        self.cardNotPresentAuth = dictionary["cardNotPresentAuth"] as? Bool
        self.customerReference = dictionary["customerReference"] as? String
        self.otherChargesAllowed = dictionary["otherChargesAllowed"] as? Bool
        self.purchaseOrder = dictionary["purchaseOrder"] as? String
        self.wifiAccessAllowed = dictionary["wifiAccessAllowed"] as? Bool
        self.dinnerAllowance = dictionary["dinnerAllowance"] as? String
    }

    var dictionary: PIDictionary {
        var parameters: PIDictionary = [:]

        parameters["alcoholAllowed"] = alcoholAllowed
        parameters["atosPassword"] = atosPassword
        parameters["atosUsername"] = atosUsername
        parameters["breakfastCode"] = breakfastCode
        parameters["carParkingAllowed"] = carParkingAllowed
        // TODO: Sending this parameter will trigger verification on atosPassword. We need to handle incorrect passwords more gracefully.
        parameters["cardNotPresentAuth"] = cardNotPresentAuth
        parameters["customerReference"] = customerReference
        parameters["otherChargesAllowed"] = otherChargesAllowed
        parameters["purchaseOrder"] = purchaseOrder
        parameters["wifiAccessAllowed"] = wifiAccessAllowed

        if let dinnerAllowance = dinnerAllowance {
            let allowance: [String: Any] = [
                "amount": NumberFormatter.gbNumberFormatter.number(from: dinnerAllowance) ?? 0,
                "currency": CostUnit.pound.rawValue
            ]
            parameters["dinnerAllowance"] = allowance
        }

        for key in parameters.keys where parameters[key] == nil {
            parameters.removeValue(forKey: key)
        }

        return parameters
    }
}

public extension NumberFormatter {
    static let gbNumberFormatter: NumberFormatter = {
        let formatter = NumberFormatter()
        formatter.locale = Locale(identifier: "en_GB")

        return formatter
    }()
}
