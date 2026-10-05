//
//  Cost.swift
//  PremierInn
//
//  Created by Marcello Mascia on 08/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation

enum CostUnit: String {
    case pound = "GBP"
    case euro = "EUR"
    case dollar = "USD"
}

private enum CostError: Error {
    case currencyCodeNormalizationFailed
    case missingCostDictionary
    case missingCostAmountValue
}

/// https://app.swaggerhub.com/apis/whitbread/Reservation-API/2.1.0#/Price
public struct Cost {
    // MARK: - Properties

    static func zeroCost(currency: String) -> Cost {
        Cost(amount: 0, currencyCode: currency)
    }
    static let zeroPounds = Cost(amount: 0, currencyCode: CostUnit.pound.rawValue)
    static let curencySymbols = ["£": "GBP", "$": "USD", "€": "EUR"]

    public var amount: NSDecimalNumber
    public var currencyCode: String
    public var locale: Locale

    public var localizedValue: String {
        let formatter = LanguageManager().formatter(currency: currencyCode)

        return formatter.string(from: amount) ?? ""
    }

    // MARK: - Init

    public init(amount: Double, currencyCode: String, locale: Locale = Locale.current) {
        self.amount = NSDecimalNumber(value: amount)
        self.currencyCode = Cost.normalizeCurrencyCode(code: currencyCode)
        self.locale = locale
    }

    init(dictionary: PIDictionary?, locale: Locale = Locale.current) throws {
        guard let dictionary = dictionary else { throw CostError.missingCostDictionary }

        self.locale = locale

        let amountValue: Double? = {
            if let value: String = dictionary.value(forKeys: ["amount", "feeAmount"]) {
                return Double(value)
            }

            if let value: Double = dictionary.value(forKeys: ["amount", "feeAmount"]) {
                return value
            }

            return nil
        }()
        guard let amount = amountValue else { throw CostError.missingCostAmountValue }
        guard let currencyCode: String = dictionary.value(forKeys: ["currency", "feeCurrency"])
            else { throw CostError.currencyCodeNormalizationFailed }

        self.amount = NSDecimalNumber(value: amount)
        self.currencyCode = Cost.normalizeCurrencyCode(code: currencyCode)
    }

    // MARK: - Private Methods

    private static func normalizeCurrencyCode(code: String) -> String {
        curencySymbols[code] ?? code
    }

    public static func + (lhs: Cost, rhs: Cost) -> Cost? {
        guard lhs.currencyCode == rhs.currencyCode else { return nil }

        return Cost(amount: lhs.amount.adding(rhs.amount).doubleValue, currencyCode: lhs.currencyCode, locale: lhs.locale)
    }

    public static func - (lhs: Cost, rhs: Cost) -> Cost? {
        guard lhs.currencyCode == rhs.currencyCode else { return nil }

        return Cost(
            amount: lhs.amount.subtracting(rhs.amount).doubleValue,
            currencyCode: lhs.currencyCode,
            locale: lhs.locale
        )
    }
}

extension Cost: Equatable {
	public static func == (lhs: Cost, rhs: Cost) -> Bool {
        lhs.amount == rhs.amount && lhs.currencyCode == rhs.currencyCode
    }
}

extension Cost: Comparable {
    public static func < (lhs: Cost, rhs: Cost) -> Bool {
        lhs.amount.doubleValue < rhs.amount.doubleValue
    }
}

extension Cost: Decodable {
    enum CodingKeys: String, CodingKey {
        case amount
        case feeAmount
        case currency
        case feeCurrency
        case locale
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        self.amount = try {
            if let value = try? container.decode(Double.self, forKeys: [.amount, .feeAmount]) {
                return NSDecimalNumber(value: value)
            }

            if let amount = try? container.decode(String.self, forKeys: [.amount, .feeAmount]), let value = Double(amount) {
                return NSDecimalNumber(value: value)
            }

            throw CostError.missingCostAmountValue
        }()
        self.currencyCode = try {
            if let value = try? container.decode(String.self, forKeys: [.currency, .feeCurrency]) {
                return Cost.normalizeCurrencyCode(code: value)
            }

            throw CostError.currencyCodeNormalizationFailed
        }()
        self.locale = Locale.current
    }
}

extension Cost: Encodable {
	public func encode(to encoder: Encoder) throws {
    }
}

extension Cost {
    static let formatter: NumberFormatter = {
        let formatter = NumberFormatter()
        formatter.decimalSeparator = "."
        formatter.alwaysShowsDecimalSeparator = true
        formatter.minimumFractionDigits = 1
        formatter.minimumIntegerDigits = 1

        return formatter
    }()

    var toDictionary: PIDictionary {
        var dict = PIDictionary()
        dict["amount"] = Cost.formatter.string(from: amount)
        dict["currency"] = currencyCode

        return dict
    }
}

extension Cost {
    var cccpAmountDic: PIDictionary? {
        guard let minorUnits = minorUnitsInt else { return nil }
        return [
            "currency": currencyCode,
            "minorUnits": minorUnits
        ]
    }

    var minorUnitsInt: Int? {
        let numberFormatter = NumberFormatter()
        numberFormatter.currencyCode = currencyCode
        numberFormatter.alwaysShowsDecimalSeparator = false
        numberFormatter.usesGroupingSeparator = false
        numberFormatter.maximumFractionDigits = 2

        guard let minorUnits = numberFormatter.string(from: amount.multiplying(by: 100)) else { return nil }

        return Int(minorUnits)
    }
}

extension Cost: Hashable {
    public func hash(into hasher: inout Hasher) {
        hasher.combine(amount.description + currencyCode + locale.description)
    }
}
