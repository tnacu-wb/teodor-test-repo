//
//  HotelDetailsDiscountCodeUseCases.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 05/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

protocol HotelDetailsDiscountCodeUseCaseFactoryType {
    func getValidateDiscountCodeUseCase(promotionCode: String) -> ValidateDiscountCodeUseCaseType
}

struct HotelDetailsDiscountCodeUseCaseFactory: HotelDetailsDiscountCodeUseCaseFactoryType {
    private let requestsManager = RequestsManager()

    func getValidateDiscountCodeUseCase(promotionCode: String) -> ValidateDiscountCodeUseCaseType {
        let criteria = PromotionsInformationCriteria(
            brand: BookingDetails.sharedInstance.hotel?.brand ?? .premierInn,
            promotionCode: promotionCode,
            bookingDate: nil,
            stayStartDate: BookingDetails.sharedInstance.criteria.arrivalDate,
            stayEndDate: BookingDetails.sharedInstance.criteria.checkOutDate ?? Date(),
            basketReference: nil,
            isPromoBox: true
        )
        return ValidateDiscountCodeUseCase(
            criteria: criteria,
            service: requestsManager
        )
    }
}
