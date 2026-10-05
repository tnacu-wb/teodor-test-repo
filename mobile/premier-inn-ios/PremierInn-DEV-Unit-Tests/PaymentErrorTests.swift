//
//  PaymentErrorTests.swift
//  PremierInnTests
//
//  Created by Louis Faria-Softly on 23/12/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

final class PaymentErrorTests: XCTestCase {

    private var basket: Basket {
        let data: PIDictionary = [
            "basketStatus": [
                "basketStatus": "FAILED",
                "basketError": [
                    "code": "PAYMENT_INCORRECT_CARD_EXCEPTION_VALUE"
                ]
            ]
        ]

        do {
            let jsonData1 = try! JSONSerialization.data(withJSONObject: data["basketStatus"] as Any, options: .prettyPrinted)
            let basketStatus = try! JSONDecoder().decode(Basket.self, from: jsonData1)
            XCTAssertNotNil(basketStatus)
            return basketStatus
        }
    }

    func testPaymentCardIncorrectError() {

        XCTAssertEqual(MSMappedError(rawValue: basket.basketError!.code!)!.errorMessage, "Your transaction was declined due to incorrect card details, please check your card details and try again")

    }
}
