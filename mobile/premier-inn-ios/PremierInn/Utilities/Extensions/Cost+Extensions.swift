//
//  Cost+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 08/07/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

extension Cost {
    func fancyMantissaString(
        baseAttributes: [NSAttributedString.Key: Any],
        mantissaAttributes: [NSAttributedString.Key: Any]
    ) -> NSAttributedString {
        let string = NSString(string: localizedValue)
        let result = NSMutableAttributedString(string: string as String, attributes: baseAttributes)

        let decimalSeparatorRange = string.range(of: NumberFormatter.defaultCurrencyFormatter.decimalSeparator)
        guard decimalSeparatorRange.location != NSNotFound else { return result }

        let mantissa = string.substring(from: decimalSeparatorRange.location)
        let mantissaRange = string.range(of: mantissa)

        result.addAttributes(mantissaAttributes, range: mantissaRange)

        return result
    }

    var toDictionary: PIDictionary { ["amount": amount, "currency": currencyCode] }

    func costDivided(by value: Double) -> Cost {
        let dividedCost = amount.doubleValue / value
        return Cost(amount: dividedCost, currencyCode: currencyCode)
    }
}
