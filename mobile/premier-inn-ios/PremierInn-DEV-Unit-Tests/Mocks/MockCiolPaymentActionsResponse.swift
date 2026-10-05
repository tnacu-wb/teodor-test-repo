//
//  MockCiolPaymentActionsResponse.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 08/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

extension CiolPaymentActionsResponse {
    static var cityTaxResponse: Self  {
        .init(
            displayPaymentPage: true,
            paymentActions: [
                .init(
                    chargeType: .creditCard,
                    price: Cost(
                        amount: 13,
                        currencyCode: "GBP"
                    )
                ),
                .init(
                    chargeType: .cardOnFile,
                    price: Cost(
                        amount: 133,
                        currencyCode: "GBP"
                    )
                )
            ]
        )
    }

    static var outstandingBalanceResponse: Self  {
        .init(
            displayPaymentPage: true,
            paymentActions: [
                .init(
                    chargeType: .creditCard,
                    price: Cost(
                        amount: 133,
                        currencyCode: "GBP"
                    )
                )
            ]
        )
    }

    static var backgroundChargeResponse: Self  {
        .init(
            displayPaymentPage: false,
            paymentActions: [
                .init(
                    chargeType: .cardOnFile,
                    price: Cost(
                        amount: 122,
                        currencyCode: "GBP"
                    )
                )
            ]
        )
    }
}
