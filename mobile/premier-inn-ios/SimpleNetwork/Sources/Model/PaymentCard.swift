//
//  PaymentCard.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/10/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

public struct CardType: Equatable {
    public let cardCode: String
    public let cardName: String?
    public let cardFee: Cost?
    public let listOrder: Int
	private let schemeLogo: String?
	public var logoURL: URL? {
		guard let schemeLogo = schemeLogo else { return nil }

		return Constants.imagesBaseURL.appendingPathComponent(schemeLogo)
	}

    public var isBusiness: Bool {
        cardCode == Constants.PaymentCardCodes.businessCard || cardCode == Constants.PaymentCardCodes.businessCardEuro
    }

    public init(cardCode: String, cardName: String?, cardFee: Cost? = nil, listOrder: Int = 100) {
        self.cardCode = cardCode
        self.cardName = cardName
        self.cardFee = cardFee
        self.listOrder = listOrder
		self.schemeLogo = nil
    }

    init(dictionary: PIDictionary) {
        self.cardCode = dictionary["code"] as? String ?? ""
        self.cardName = dictionary["name"] as? String
        self.listOrder = Int(dictionary["listOrder"] as? String ?? "100") ?? 100
        self.cardFee = try? Cost(dictionary: dictionary)
		self.schemeLogo = dictionary["schemeLogo"] as? String
    }

    public static func == (lhs: CardType, rhs: CardType) -> Bool {
        lhs.cardCode == rhs.cardCode
    }
}

public typealias BBStoredCardParams = (id: String, type: PaymentMethodType)

public struct TokenisedCardInfo: Codable {
    let token: String
    let type: String
}

public enum PaymentCardError: Error {
    case missingCardDictionary
    case missingCardNumber
    case wrongExpiryDateFormat
    case missingCardHolder
    case missingCardCode
}

public struct PaymentCard {
	public static let empty = PaymentCard()
	private static let chunkSize = 4
	private static let maskChar = "*"

    public let cardId: String?
    public let cardNumber: String
    public let startDate: Date?
    public let expiryDate: Date?
    public let issueNumber: Int?
    public let cardholderName: String
    public let cardLabel: String?
    public var cardType: CardType
    public var token: String?
    let paymentOnly: Bool

    public var address: Address?
    public var isBusiness: Bool {
    	cardType.cardCode == Constants.PaymentCardCodes.businessCard || cardType.cardCode == Constants.PaymentCardCodes
    	.businessCardEuro }
	public var cardNumberMasked: String {
		if cardNumber.isEmpty { return "" }

		let cleanCardNumber = cardNumber.filter { $0 != " " }

		// Let's go nuts with function chaining
		return cleanCardNumber
			.prefix(cleanCardNumber.count - PaymentCard.chunkSize) // Take the string without the last 4 chars
			.enumerated() // Enumerate because we need the index of each char
			.map {
				$0.offset % PaymentCard.chunkSize == PaymentCard.chunkSize - 1 ? PaymentCard.maskChar + " " : PaymentCard
				.maskChar } // Return the mask or an additional space on the 4th char
			.joined() // Join the elements of the array
			.trimmingCharacters(in: .whitespacesAndNewlines) // Trim additional spaces
			+ " " // Add one space manually
			+ cleanCardNumber.suffix(PaymentCard.chunkSize) // Add the last unmasked 4 chars
	}

    var cardTypeImage: UIImage? { UIImage(named: cardType.cardCode) }
    public var isMasked: Bool { cardNumber.contains(PaymentCard.maskChar) }
    public var cardNotPresentRequired: Bool?

    public static func date(parameter: String?) -> Date? {
        guard let stringValue = parameter else { return nil }

        if let date = DateFormatter.expiryDateFormatter.date(from: stringValue) {
            return date
        }

        if let date = DateFormatter.alternateExpiryDateFormatter.date(from: stringValue) {
            var dateComponents = Calendar.current.dateComponents([.year, .month], from: date)
            guard let year = dateComponents.year, year < 1000 else { return date }
            dateComponents.year = ((dateComponents.year ?? 0) + 2000)

            return Calendar.current.date(from: dateComponents)
        }

        if let date = DateFormatter.creditCardDateFormatter.date(from: stringValue) {
            var dateComponents = Calendar.current.dateComponents([.year, .month], from: date)
            guard let year = dateComponents.year, year < 1000 else { return date }
            dateComponents.year = ((dateComponents.year ?? 0) + 2000)

            return Calendar.current.date(from: dateComponents)
        }

        return nil
    }

    public func expired(onDate date: Date) -> Bool {
		guard let expiryDate = expiryDate else { return false }

		return (expiryDate.endOfMonth() < date)
    }
}

extension PaymentCard: Equatable {
	public static func == (lhs: PaymentCard, rhs: PaymentCard) -> Bool {
        lhs.cardNumber == rhs.cardNumber && lhs.cardType == rhs.cardType && lhs.expiryDate == rhs.expiryDate
	}
}

extension PaymentCard {
	public init(cardNumber: String, expiryDate: Date?, cardholderName: String, cardName: String, cardCode: String) {
        self.cardId = nil
		self.cardNumber = cardNumber
		self.expiryDate = expiryDate
		self.cardholderName = cardholderName
		self.cardType = CardType(cardCode: cardCode, cardName: cardName, cardFee: nil)
		self.startDate = nil
		self.issueNumber = nil
		self.paymentOnly = false
        self.cardLabel = nil
        self.token = nil
	}

	public init(dictionary: PIDictionary?) throws {
		guard let dictionary = dictionary else { throw PaymentCardError.missingCardDictionary }

		guard let cardNumber = dictionary["cardNumber"] as? String else { throw PaymentCardError.missingCardNumber }
        self.cardNumber = cardNumber

        guard let expiryDate = PaymentCard.date(parameter: dictionary["expiryDate"] as? String)?.endOfMonth()
        	else { throw PaymentCardError.wrongExpiryDateFormat }
        self.expiryDate = expiryDate

		guard let cardCode = dictionary["cardType"] as? String else { throw PaymentCardError.missingCardCode }
		guard let cardHolderName: String = dictionary.value(forKeys: ["cardholderName", "cardHolderName"])
			else { throw PaymentCardError.missingCardHolder }

        self.cardId = {
            guard let idString: String = dictionary.value(forKeys: ["cardID", "cardId"]) else { return nil }
            return idString
        }()
		self.cardholderName = cardHolderName
		self.cardType = CardType(
			cardCode: cardCode,
			cardName: dictionary["cardName"] as? String,
			cardFee: try? Cost(dictionary: dictionary)
		)
		self.paymentOnly = dictionary["paymentOnly"] as? Bool ?? false
		self.issueNumber = dictionary["issueNumber"] as? Int
		self.startDate = PaymentCard.date(parameter: dictionary["startDate"] as? String)
        self.cardNotPresentRequired = {
            if let 🤔 = dictionary["cardNotPresentRequired"] as? Bool { return 🤔 }
            if let 🤪 = dictionary["cnpRequired"] as? Bool { return 🤪 }

            if let businessAccount = dictionary["businessAccount"] as? PIDictionary {
                if let businessCNP = businessAccount["cardNotPresentAuth"] as? Bool { return businessCNP }
            }

            return nil
        }()
        self.cardLabel = dictionary["cardLabel"] as? String
        self.token = dictionary["token"] as? String
	}

	init() {
        self.cardId = nil
		self.cardNumber = ""
		self.expiryDate = nil
		self.cardholderName = ""
		self.cardType = CardType(cardCode: "", cardName: "", cardFee: nil)
		self.startDate = nil
		self.issueNumber = nil
        self.cardLabel = nil
		self.paymentOnly = false
        self.token = nil
	}

    public func isValid() -> Bool {
		!cardNumber.isEmpty && expiryDate != nil && !cardholderName.isEmpty && !cardType.cardCode.isEmpty
    }
}

extension PaymentCard: Decodable {
    enum CodingKeys: String, CodingKey {
        case cardId
        case cardNumber
        case startDate
        case expiryDate
        case issueNumber
        case nameOnCard
        case cardType
        case cardNotPresentRequired
        case cnpRequired
        case cardLabel
        case token
        case businessAccount
        case billingAddress
    }

    enum BusinessAccountKeys: String, CodingKey {
        case cardNotPresentAuth
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        self.cardId = {
            guard let cardId = try? container.decode(String.self, forKey: .cardId) else { return nil }
            return cardId
        }()

        do {
            self.cardNumber = try container.decode(String.self, forKey: .cardNumber)
            self.cardholderName = try container.decode(String.self, forKey: .nameOnCard)

            let cardTypeString = try container.decode(String.self, forKey: .cardType)
            self.cardType = CardType(cardCode: cardTypeString, cardName: nil)
        } catch {
            throw PaymentCardError.missingCardNumber
        }

        self.startDate = {
            guard let dateString = try? container.decode(String.self, forKey: .startDate) else { return nil }
            return PaymentCard.date(parameter: dateString)?.endOfMonth()
        }()
        self.expiryDate = {
            guard let dateString = try? container.decode(String.self, forKey: .expiryDate) else { return nil }
            return PaymentCard.date(parameter: dateString)?.endOfMonth()
        }()

        self.issueNumber = try? container.decode(Int.self, forKey: .issueNumber)
        self.cardLabel = try? container.decode(String.self, forKey: .cardLabel)
        self.paymentOnly = true
        self.cardNotPresentRequired = {
            if let 🤔 = try? container.decode(Bool.self, forKey: .cardNotPresentRequired) { return 🤔 }
            if let 🤪 = try? container.decode(Bool.self, forKey: .cnpRequired) { return 🤪 }

            if let businessAcount = try? container.nestedContainer(
            	keyedBy: BusinessAccountKeys.self,
            	forKey: .businessAccount
            ) {
                if let cardNotPresentAuth = try? businessAcount.decode(Bool.self, forKey: .cardNotPresentAuth) {
                    return cardNotPresentAuth
                }
            }

            return nil
        }()
        self.token = try? container.decode(String.self, forKey: .token)
        self.address = try? container.decode(Address.self, forKey: .billingAddress)
    }
}

extension PaymentCard: Encodable {
    public func encode(to encoder: Encoder) throws {}
}
