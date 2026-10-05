//
//  ValidateDiscountCodeResult+Mock.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 06/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

extension ValidateDiscountCodeResult {
    static var success: Self {
        .init(
            promotionCode: "FX20RU",
            promoBox: .init(
                whenEmpty: "someStringHere",
                whenInvalid: "someStringHere",
                whenMultipleRedeem: "someStringHere",
                whenSuccess: "someStringHere",
                whenCodeAlreadyApplied: "someStringHere",
                whenUnavailable: "someStringHere",
                whenCodeExpired: "someStringHere"
            ),
            promoKind: "UNIQUE",
            promoBoxStatus: .success,
            promoBoxMessageKey: .whenSuccess
        )
    }

    static var error: Self {
        .init(
            promotionCode: "FX20RU",
            promoBox: .init(
                whenEmpty: "someStringHere",
                whenInvalid: "someStringHere",
                whenMultipleRedeem: "someStringHere",
                whenSuccess: "someStringHere",
                whenCodeAlreadyApplied: "someStringHere",
                whenUnavailable: "someStringHere",
                whenCodeExpired: "someStringHere"
            ),
            promoKind: "UNIQUE",
            promoBoxStatus: .codeAlreadyApplied,
            promoBoxMessageKey: .whenUnavailable
        )
    }
}
