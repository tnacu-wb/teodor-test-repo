//
//  Promotions+Analytics.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 05/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

protocol AnalyticsPromotionsTrackable {
    func getPromotionsAnalyticsDict(with bookingDetails: BookingDetails?) -> PIDictionary?
}

extension AnalyticsManager: AnalyticsPromotionsTrackable {
    func getPromotionsAnalyticsDict(with bookingDetails: BookingDetails?) -> PIDictionary? {
        guard bookingDetails?.isPromotionalBooking == true else {
            return nil
        }

        var dict: PIDictionary = [:]

        let promo: (code: String, name: String?)? = {
            if let code = bookingDetails?.appIncentivePromoCode {
                return (code, PIAnalytics.PromoNames.appIncentive)
            }

            if let code = bookingDetails?.freeBreakfastPromoCode {
                return (code, PIAnalytics.PromoNames.freeBreakfast)
            }

            if let code = bookingDetails?.siteWidePromoCode {
                return (code, bookingDetails?.promoKind)
            }

            if let code = bookingDetails?.userEnteredPromoCode {
                return (code, bookingDetails?.promoKind)
            }

            return nil
        }()

        if let promo {
            dict[PIAnalytics.Keys.promoCode] = promo.code
            dict[PIAnalytics.Keys.promoName] = promo.name
            dict[PIAnalytics.Keys.promoCodeApplied] = true
        }

        if let promoRateTags = bookingDetails?.promoRateTags {
            dict[PIAnalytics.Keys.promoRateTags] = promoRateTags
        }

        return dict
    }
}
