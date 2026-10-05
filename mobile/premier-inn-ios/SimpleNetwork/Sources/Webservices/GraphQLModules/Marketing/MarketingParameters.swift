//
//  MarketingParameters.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 02/05/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static func getMarketingPreferencesParameters(
        for emailAddress: String,
        and brandCodes: MarketingBrandCode,
        isBusiness: Bool
    ) -> PIDictionary {
        var dict = PIDictionary()

        dict["business"] = isBusiness
        dict["brandCodes"] = brandCodes.rawValue
        dict["contactType"] = Constants.emailContactType
        dict["contactValue"] = emailAddress

       return ["request": dict]
    }

    static func updateMarketingPreferencesParameters(
        for brandCodes: [MarketingBrandCode],
        emailAddress: String,
        optIn: Bool,
        isoCountryCode: String
    ) -> PIDictionary {
        var dict = PIDictionary()

        // Copying logic from rest call, double opt in based on device lang
        let doubleOptIn = isoCountryCode == SupportedLanguage.german.countryCode.uppercased()

        dict["optIn"] = optIn
        dict["doubleOptIn"] = doubleOptIn
        dict["brandCodes"] = [brandCodes.first?.rawValue ?? ""]
        dict["customer"] = [
            "language": LanguageManager.supportedLanguage.rawValue,
            "countryOfResidence": isoCountryCode,
            "customerId": emailAddress
        ]

        let language = LanguageManager.supportedLanguage.analyticsCode
        dict["sourceDetails"] = [
            "channel": Constants.CCC.channelIOS,
            "journey": Constants.emailJourney,
            "locale": language
        ]

        return dict
    }
}
