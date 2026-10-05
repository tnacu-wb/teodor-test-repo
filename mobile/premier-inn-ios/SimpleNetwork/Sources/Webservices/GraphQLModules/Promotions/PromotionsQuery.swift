//
//  PromotionsQuery.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 27/11/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    /// To get informations about the promotions. Used for Site-Wide promotions.
    static let promotionsInformationQuery =
    """
    query promotionsInformation($promotionsInformationCriteria: PromotionsInformationCriteria!) {
          promotionsInformation(promotionsInformationCriteria: $promotionsInformationCriteria) {
        showPromo,
        isWithinPromoWindow,
        promotionCode,
        promoKind,
        appPromoBannerTitle,
        appPromoBannerSubtitle,
        appPromoInvalidMessage,
        appPromoExpiredMessage,
        appPromoAmendMessage,
        promoBookingInfo {
            promotionCode
            ratePlanCode
        }
        termsLink
      }
    }
    """

    /// To validate the discount code entered by the user in HDP discount modal.
    static let validateDiscountCode =
    """
    query validateDiscountCode($promotionsInformationCriteria: PromotionsInformationCriteria!) {
      promotionsInformation(promotionsInformationCriteria: $promotionsInformationCriteria) {
        promotionCode
        promoBox {
            whenEmpty
            whenInvalid
            whenMultipleRedeem
            whenSuccess
            whenCodeAlreadyApplied
            whenUnavailable
            whenCodeExpired
        }
        promoKind
        promoBoxStatus
        promoBoxMessageKey
     }
    }
    """
}
