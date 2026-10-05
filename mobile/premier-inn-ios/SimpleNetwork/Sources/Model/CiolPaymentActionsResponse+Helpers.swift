//
//  CiolPaymentActionsResponse+Helpers.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 03/06/2026.
//

import Foundation

public extension CiolPaymentActionsResponse {
    /// Returns the city tax if both `.creditCard` and `.cardOnFile` payment actions exist,
    /// and the `.creditCard` price is greater than 0; otherwise returns `nil`.
    var cityTax: Cost? {
        let hasCreditCard = paymentActions?.contains { $0.chargeType == .creditCard } == true
        let hasCardOnFile = paymentActions?.contains { $0.chargeType == .cardOnFile } == true

        guard hasCreditCard && hasCardOnFile,
              let creditCardPrice = paymentActions?
                  .first(where: { $0.chargeType == .creditCard })?
                  .price,
              creditCardPrice.amount.doubleValue > 0 else {
            return nil
        }

        return creditCardPrice
    }

    var outstandingBalance: Cost? {
        let hasCreditCard = paymentActions?.contains { $0.chargeType == .creditCard } == true
        let hasCardOnFile = paymentActions?.contains { $0.chargeType == .cardOnFile } == true

        guard hasCreditCard && !hasCardOnFile,
              let outstandingBalance = paymentActions?
                  .first(where: { $0.chargeType == .creditCard || $0.chargeType == .authorizeCard })?
                  .price
        else {
            return nil
        }

        return outstandingBalance
    }

    var shouldPerformBackgroundCharge: Bool {
        paymentActions?.contains { $0.chargeType == .cardOnFile } == true
    }
}
