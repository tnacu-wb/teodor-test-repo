//
//  PaymentCard+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

extension PaymentCard {
    func accessibilityDescription(cardName: String? = nil, usageDescription: String? = nil) -> String {
        [
            nonEmpty(cardName) ?? nonEmpty(cardNameDescription),
            cardNumberAccessibilitySummary,
            expiryDateAccessibilityFormat,
            nonEmpty(usageDescription)
        ].compactMap { $0 }.joined(separator: ", ")
    }

    var cardNumberSummary: String {
        guard cardNumber.count > 4 else {
            return ""
        }

        return PILocalizedString("paymentCardNumberSummary") +
        " " +
        cardNumber.substringFromIndex(cardNumber.count - 4)
    }

    var expiryDateCardFormat: String {
        guard let expiryDate = expiryDate else {
            return ""
        }

        if expiryDate >= Date() {
            return PILocalizedString("paymentCardFutureExpiration") +
            " " +
            expiryDate.creditCardDateFormat
        } else {
            return PILocalizedString("paymentCardPastExpiration") +
            " " +
            expiryDate.creditCardDateFormat
        }
    }

    var cv2DigitsLength: Int {
        [SimpleNetwork.Constants.PaymentCardCodes.amex].contains(cardType.cardCode) ? 4 : 3
    }

    var storedBusinessOrPersonalCardLabel: String {
        guard self == UserSessionManager.sharedInstance.currentUser?.centrallyStoredBusinessCard else {
            return PILocalizedString("paymentMethodsPersonalCard")
        }

        return PILocalizedString("paymentMethodsStoredBusinessCard")
    }

    var cardNameDescription: String {
        guard let cardFee = cardType.cardFee else { return cardType.cardName }

        var result: String = cardType.cardName

        if cardFee.localizedValue.isEmpty == false {
            result += " (\(cardFee.localizedValue))"
        }

        return result
    }
}

private extension PaymentCard {
    func nonEmpty(_ string: String?) -> String? {
        let trimmedString = string?.trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmedString?.isEmpty == false ? trimmedString : nil
    }

    var cardNumberAccessibilitySummary: String? {
        guard let lastFourDigits = cardNumberLastFourDigits else {
            return nil
        }

        return PILocalizedString("paymentCardNumberSummary") +
        " " +
        lastFourDigits
    }

    var cardNumberLastFourDigits: String? {
        let digits = cardNumber.filter {
            $0.isNumber
        }

        guard digits.count >= 4 else {
            return nil
        }

        return String(digits.suffix(4))
    }

    var expiryDateAccessibilityFormat: String? {
        guard let expiryDate else {
            return nil
        }

        let expiryState = expiryDate >= Date() ?
        "paymentCardFutureExpiration" :
        "paymentCardPastExpiration"

        return PILocalizedString(expiryState) + " " +
        DateFormatter.paymentCardAccessibilityExpiryFormatter.string(from: expiryDate)
    }

    var cardTypeMappedName: String {
        Constants.CardTypeConfigMapper(rawValue: cardType.cardCode)?.cardName ?? cardType.cardCode
    }
}

private extension DateFormatter {
    static let paymentCardAccessibilityExpiryFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale.current
        formatter.setLocalizedDateFormatFromTemplate("MMMM yyyy")

        return formatter
    }()
}
