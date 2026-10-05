//
//  HotelSearchParameters.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static func getHotelParameters(hotelCode: String) -> PIDictionary {
        var params = PIDictionary()

        params["hotelId"] = hotelCode
        params["language"] = LanguageManager.supportedLanguage.rawValue
        params["country"] = LanguageManager.supportedLanguage.countryCode
        params["category"] = LabelsConfig.Constants.mainCategory
        params["labels"] = LabelsConfig.roomDisclaimer.labels

        return params
    }

    static func headerInformationVariables() -> PIDictionary {
        var params = PIDictionary()

        params["country"] = LanguageManager.supportedLanguage.countryCode
        params["language"] = LanguageManager.supportedLanguage.rawValue

        return params
    }

    static func roomTypeInformationVariables() -> PIDictionary {
        var params = PIDictionary()

        params["brand"] = BookingDetails.sharedInstance.hotel?.brand.rawValue.lowercased() ?? HotelBrand.premierInn.rawValue
            .lowercased()
        params["country"] = LanguageManager.supportedLanguage.countryCode
        params["language"] = LanguageManager.supportedLanguage.rawValue

        return params
    }

    static func ratesInformationVariables(ratePlans: [String], hotelCode: String, hotelBrand: HotelBrand?) -> PIDictionary {
        var params = PIDictionary()

        params["brand"] = hotelBrand?.rawValue.lowercased() ?? HotelBrand.premierInn.rawValue.lowercased()
        params["country"] = LanguageManager.supportedLanguage.countryCode
        params["language"] = LanguageManager.supportedLanguage.rawValue
        params["hotelId"] = hotelCode

        let channel = BookingDetails.sharedInstance.bookingMode == .business ? Channel.BB.rawValue : Channel.PI.rawValue
        params["channel"] = channel

        params["ratePlans"] = ratePlans

        return params
    }

    static func getCountriesVariables() -> PIDictionary {
        var params = PIDictionary()

        params["site"] = BookingDetails.sharedInstance.bookingMode.rawValue
        params["language"] = LanguageManager.supportedLanguage.rawValue
        params["country"] = LanguageManager.supportedLanguage.countryCode

        return params
    }

    static func slugBasedHotelInformationsVariables(slug: String) -> [String: Any] {
        var params = [String: Any]()

        params["slug"] = slug
        params["country"] = LanguageManager.supportedLanguage.countryCode
        params["language"] = LanguageManager.supportedLanguage.rawValue

        return params
    }

    static func homepageContentVariables(
        channel: Channel,
        subchannel: String,
        language: String,
        country: String
    ) -> [String: Any] {
        var params = [String: Any]()

        params["channel"] = channel.rawValue
        params["subchannel"] = subchannel
        params["country"] = country
        params["language"] = language

        return params
    }
}
