//
//  ValidateDiscountCodeResultTests.swift
//  SimpleNetworkTests
//
//  Created by Rodrigues, Seymour (Contractor) on 06/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Testing
@testable import SimpleNetwork

struct ValidateDiscountCodeResultTests {

    private var sut: ValidateDiscountCodeResult!

    @Test(
        "Tests to cover the returned string for `messsage` computed property",
        arguments: ValidateDiscountCodeResult.PromoBoxMessageKey.allCases + [nil]
    )
    mutating func testMessage(messageKey: ValidateDiscountCodeResult.PromoBoxMessageKey?) {
        // GIVEN sut
        sut = createSut(withMessageKey: messageKey)

        // WHEN `message` is stored to be checked
        let result = sut.message

        // THEN
        switch messageKey {
        case .whenEmpty:
            #expect(result == Constants.whenEmpty)
        case .whenInvalid:
            #expect(result == Constants.whenInvalid)
        case .whenMultipleRedeem:
            #expect(result == Constants.whenMultipleRedeem)
        case .whenSuccess:
            #expect(result == Constants.whenSuccess)
        case .whenCodeAlreadyApplied:
            #expect(result == Constants.whenCodeAlreadyApplied)
        case .whenUnavailable:
            #expect(result == Constants.whenUnavailable)
        case .whenCodeExpired:
            #expect(result == Constants.whenCodeExpired)
        case nil:
            #expect(result == nil)
        }
    }

}

private extension ValidateDiscountCodeResultTests {

    enum Constants {
        static let whenEmpty = "Voucher code is required"
        static let whenInvalid = "You entered an invalid code, please Check the code and try again"
        static let whenMultipleRedeem = "You cannot redeem two codes during the same bookings"
        static let whenSuccess = "Voucher applied successfully"
        static let whenCodeAlreadyApplied = "Promotion already applied"
        static let whenUnavailable = "Promo code unavailable for this stay"
        static let whenCodeExpired = "This code has expired and can no longer be used"
    }

    func createSut(
        withMessageKey messageKey: ValidateDiscountCodeResult.PromoBoxMessageKey?
    ) -> ValidateDiscountCodeResult {
        .init(
            promotionCode: "FX20RU",
            promoBox: .init(
                whenEmpty: Constants.whenEmpty,
                whenInvalid: Constants.whenInvalid,
                whenMultipleRedeem: Constants.whenMultipleRedeem,
                whenSuccess: Constants.whenSuccess,
                whenCodeAlreadyApplied: Constants.whenCodeAlreadyApplied,
                whenUnavailable: Constants.whenUnavailable,
                whenCodeExpired: Constants.whenCodeExpired
            ),
            promoKind: "UNIQUE",
            promoBoxStatus: .success,
            promoBoxMessageKey: messageKey
        )
    }

}
