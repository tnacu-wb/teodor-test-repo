//
//  PromotionsInformation.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 27/11/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation

public struct PromotionsInformation: Codable {
    public let showPromo: Bool
    public let isWithinPromoWindow: Bool
    public let promotionCode: String?
    public let promoKind: String?
    public let appPromoBannerTitle: String?
    public let appPromoBannerSubtitle: String?
    public let appPromoInvalidMessage: String?
    public let appPromoExpiredMessage: String?
    public let appPromoAmendMessage: String?
    public let termsLink: String?
    public let promoBookingInfo: PromoBookingInfo?
}

public struct PromoBookingInfo: Codable {
    public let ratePlanCode: String?
    public let promotionCode: String?
}
