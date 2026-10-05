//
//  PromotionsInformationCriteria.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 05/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

public struct PromotionsInformationCriteria {
    let brand: HotelBrand
    let promotionCode: String?
    let bookingDate: Date?
    let stayStartDate: Date
    let stayEndDate: Date
    let basketReference: String?
    let isPromoBox: Bool?

    public init(
        brand: HotelBrand,
        promotionCode: String?,
        bookingDate: Date?,
        stayStartDate: Date,
        stayEndDate: Date,
        basketReference: String?,
        isPromoBox: Bool? = nil
    ) {
        self.brand = brand
        self.promotionCode = promotionCode
        self.bookingDate = bookingDate
        self.stayStartDate = stayStartDate
        self.stayEndDate = stayEndDate
        self.basketReference = basketReference
        self.isPromoBox = isPromoBox
    }
}
