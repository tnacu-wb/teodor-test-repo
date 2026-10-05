//
//  PaymentOptions.swift
//  SimpleNetwork
//
//  Created by Freddie Parks on 23/09/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import Foundation

public struct PaymentSettlementOption: Codable {
    public let type: String
    public let enabled: Bool
}

public struct PaymentOption: Decodable {
    public let name: String
    public let type: String
    public let logoSrc: String?
    public let order: Int
    public let acceptedCardTypes: [AcceptedCardType]?
    public let enabled: Bool
    public let cnpOptionAvailable: Bool
    public let card: Card?
    public let paymentOptions: [PaymentSettlementOption]?
    public let reasons: [String]?
    public let clientToken: String?
    public let subType: String?

    public var acceptedCardAccessibilityLabel: String? {
        let acceptedCardNames = acceptedCardTypes?.map({ $0.name })
        return acceptedCardNames?.joined(separator: " ")
    }
}

public struct AcceptedCardType: Decodable {
    public let type: String
    public let logoUrl: String?
    public let name: String

    enum CodingKeys: String, CodingKey {
        case type
        case logoUrl
        case logoSrc
        case name
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        self.type = try container.decode(String.self, forKey: .type)
        self.logoUrl = try container.decode(String.self, forKeys: [.logoUrl, .logoSrc])
        self.name = try container.decode(String.self, forKey: .name)
    }
}

public struct Card {
    public let token: String
    public let expiryMonth: String
    public let expiryYear: String
    public let type: CardType
    public let logoUrl: String
    public let cardholderName: String
    public let cardType: String
    public let cnpRequired: Bool
}

public enum PaymentOptionsError: LocalizedError {
    case unknown
    case noPaymentOptions
    case missingHotelCode
    case missingRateRules

    public var localizedDescription: String { String(describing: self) }
    public var errorDescription: String? { String(describing: self) }
}

public struct PaymentMethodsResponse: Decodable {
    public let paymentMethodsAvailable: Bool
    public let paymentMethods: [PaymentOption]?
}

extension Card: Codable {
    enum CodingKeys: String, CodingKey {
        case token
        case expiryMonth
        case expiryYear
        case type
        case logoUrl
        case logoSrc
        case cardholderName
        case cardHolderName
        case cardType
        case cnpRequired
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        do {
            self.token = try container.decode(String.self, forKey: .token)
            self.expiryMonth = try container.decode(String.self, forKey: .expiryMonth)
            self.expiryYear = try container.decode(String.self, forKey: .expiryYear)
            self.logoUrl = try container.decode(String.self, forKeys: [.logoUrl, .logoSrc])
            self.cardholderName = try container.decode(String.self, forKeys: [.cardholderName, .cardHolderName])
            self.cardType = try container.decode(String.self, forKey: .cardType)
            self.cnpRequired = try container.decode(Bool.self, forKey: .cnpRequired)

            let cardTypeString = try container.decode(String.self, forKey: .type)
            self.type = CardType(cardCode: cardTypeString, cardName: nil)
        } catch {
            throw PaymentCardError.missingCardNumber
        }
    }

    public func encode(to encoder: Encoder) throws {}
}

extension PaymentOption: Equatable {
    public static func == (lhs: PaymentOption, rhs: PaymentOption) -> Bool {
        lhs.order == rhs.order && lhs.card?.token == rhs.card?.token && lhs.type == rhs.type
    }
}

public extension PaymentOption {
    var paymentMethodType: PaymentMethodType? {
        // If the payment type is PIBA euro then use a custom type
        let type = subType == Constants.PIBAEuro.subType ? Constants.PIBAEuro.customType : type
        guard let aPaymentMethodType = PaymentMethodType(rawValue: card?.cardType ?? type) else { return nil }

        return aPaymentMethodType
    }

    var paymentType: CCCPPaymentType? {
        switch paymentMethodType {
        case .applePay:
            return .APPLEPAY
        case .newBAC, .newBACEuro:
            return .PIBA
        case .newCreditDebitCard:
            return .CARD
        case .stored, .storedCompanyBB, .storedPersonalBB:
            return card?.type.isBusiness == true ? .PIBA : .CARD
        case .paypal:
            return .PAYPAL
        case .reserveWithoutCard:
            return .RESERVE_WITHOUT_CARD
        case .none:
            return nil
        }
    }
}

extension Card {
    var cccpDic: PIDictionary? {
        guard token.isEmpty == false else { return nil }
        var dictionary: PIDictionary = [
            "token": token,
            "cardholderName": cardholderName
        ]

        if expiryMonth.isEmpty == false && expiryYear.isEmpty == false {
            dictionary["expiryMonth"] = expiryMonth
            dictionary["expiryYear"] = expiryYear
        }

        return dictionary
    }
}
