//
//  ValidateDiscountCodeResult.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 05/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

// MARK: - ValidateDiscountCodeResult

public struct ValidateDiscountCodeResult: Decodable {
    public let promotionCode: String?
    public let promoBox: PromoBox?
    public let promoKind: String?
    public let promoBoxStatus: PromoBoxStatus?
    public let promoBoxMessageKey: PromoBoxMessageKey?

    public init(
        promotionCode: String?,
        promoBox: PromoBox?,
        promoKind: String?,
        promoBoxStatus: PromoBoxStatus?,
        promoBoxMessageKey: PromoBoxMessageKey?
    ) {
        self.promotionCode = promotionCode
        self.promoBox = promoBox
        self.promoKind = promoKind
        self.promoBoxStatus = promoBoxStatus
        self.promoBoxMessageKey = promoBoxMessageKey
    }

    public var message: String? {
        guard let promoBoxMessageKey else { return nil }

        return switch promoBoxMessageKey {
        case .whenEmpty: promoBox?.whenEmpty
        case .whenInvalid: promoBox?.whenInvalid
        case .whenMultipleRedeem: promoBox?.whenMultipleRedeem
        case .whenSuccess: promoBox?.whenSuccess
        case .whenCodeAlreadyApplied: promoBox?.whenCodeAlreadyApplied
        case .whenUnavailable: promoBox?.whenUnavailable
        case .whenCodeExpired: promoBox?.whenCodeExpired
        }
    }
}

// MARK: - PromoBox

public extension ValidateDiscountCodeResult {
    struct PromoBox: Decodable {
        public let whenEmpty, whenInvalid, whenMultipleRedeem, whenSuccess: String?
        public let whenCodeAlreadyApplied, whenUnavailable, whenCodeExpired: String?

        public init(
            whenEmpty: String?,
            whenInvalid: String?,
            whenMultipleRedeem: String?,
            whenSuccess: String?,
            whenCodeAlreadyApplied: String?,
            whenUnavailable: String?,
            whenCodeExpired: String?
        ) {
            self.whenEmpty = whenEmpty
            self.whenInvalid = whenInvalid
            self.whenMultipleRedeem = whenMultipleRedeem
            self.whenSuccess = whenSuccess
            self.whenCodeAlreadyApplied = whenCodeAlreadyApplied
            self.whenUnavailable = whenUnavailable
            self.whenCodeExpired = whenCodeExpired
        }
    }
}

// MARK: - PromoBoxStatus

public extension ValidateDiscountCodeResult {
    enum PromoBoxStatus: String, CodingKey, Decodable {
        case empty = "EMPTY"
        case invalid = "INVALID"
        case codeAlreadyApplied = "CODE_ALREADY_APPLIED"
        case codeExpired = "CODE_EXPIRED"
        case unavilable = "UNAVAILABLE"
        case success = "SUCCESS"
    }
}

// MARK: - PromoBoxMessageKey

public extension ValidateDiscountCodeResult {
    enum PromoBoxMessageKey: String, CodingKey, Decodable, CaseIterable {
        case whenEmpty, whenInvalid, whenMultipleRedeem, whenSuccess
        case whenCodeAlreadyApplied, whenUnavailable, whenCodeExpired
    }
}
