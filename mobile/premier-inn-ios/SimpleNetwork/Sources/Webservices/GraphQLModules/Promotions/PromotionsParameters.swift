//
//  PromotionsParameters.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 27/11/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static func promotionsInformationVariables(criteria: PromotionsInformationCriteria) throws -> PIDictionary {
        var dict = PIDictionary()

        dict["country"] = LanguageManager.supportedLanguage.countryCode
        dict["language"] = LanguageManager.supportedLanguage.rawValue

        let channel = BookingDetails.sharedInstance.bookingMode == .business ? Channel.BB.rawValue : Channel.PI.rawValue
        dict["channel"] = channel
        dict["brand"] = criteria.brand.rawValue

        dict["stayStartDate"] = criteria.stayStartDate.parameterString
        dict["stayEndDate"] = criteria.stayEndDate.parameterString

        if let promotionCode = criteria.promotionCode {
            dict["promotionCode"] = promotionCode
        }

        if let basketReference = criteria.basketReference {
            dict["basketReference"] = basketReference
        }

        if let bookingDate = criteria.bookingDate {
            dict["bookingDate"] = bookingDate.parameterString
        }

        if let isPromoBox = criteria.isPromoBox {
            dict["isPromoBox"] = isPromoBox
        }

        return ["promotionsInformationCriteria": dict]
    }
}
