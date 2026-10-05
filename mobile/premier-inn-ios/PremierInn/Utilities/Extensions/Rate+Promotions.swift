//
//  Rate+Promotions.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 08/07/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

// MARK: - Rate + Promotions

extension Rate {
    // MARK: - promotionTag

    func promotionTag(
        for hotelBrand: HotelBrand?,
        settingsManager: SettingsManager = .sharedInstance,
        bookingDetails: BookingDetails = .sharedInstance
    ) -> String? {
        guard let classification else {
            return nil
        }

        // Employee offer
        if classification == SimpleNetwork.Constants.EmployeeOffer.rateClassification {
            return PILocalizedString("hotelDetailsEmployeeRate")
        }

        // App incentive
        if hasPromoCode(matching: settingsManager.appIncentivePromoCode) {
            return PILocalizedString("appDiscountTag")
        }

        // Free breakfast, site-wide or user entered
        if hasAnyRateContentPromoCode(
            settingsManager: settingsManager,
            bookingDetails: bookingDetails
        ) {
            return settingsManager
                .rateContent(for: classification, and: hotelBrand)?
                .rateTags?
                .compactMap(\.self)
                .first
        }

        // No promotion
        return nil
    }
}

// MARK: - promotionTag helpers

private extension Rate {
    func hasPromoCode(matching code: String?) -> Bool {
        guard let promotionCode, promotionCode.isNotEmpty else {
            return false
        }

        return promotionCode == code
    }

    func hasAnyRateContentPromoCode(
        settingsManager: SettingsManager,
        bookingDetails: BookingDetails
    ) -> Bool {
        hasPromoCode(matching: bookingDetails.freeBreakfastPromoCode) ||
        hasPromoCode(matching: settingsManager.siteWidePromotionContent?.promotionCode) ||
        hasPromoCode(matching: bookingDetails.userEnteredPromoCode)
    }
}
