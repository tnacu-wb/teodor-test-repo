//
//  LanguageManager.swift
//  SimpleNetwork
//
//  Created by Simon Antoine on 02/11/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import Foundation

public enum SupportedLanguage: String {
    case english = "en"
    case german = "de"

    public var countryCode: String {
        switch self {
        case .english:
            return "gb"
        case .german:
            return "de"
        }
    }

    public var analyticsCode: String {
        switch self {
        case .english:
            return "UK"
        case .german:
            return "DE"
        }
    }
}

public class LanguageManager {
    public static let sharedInstance = LanguageManager()

    public static var supportedLanguage: SupportedLanguage = {
        let languageCode = Locale.current.language.languageCode?.identifier.lowercased() ?? "en"
        let language = SupportedLanguage(rawValue: languageCode) ?? .english

        return language
    }()

    public static var localisationParameters: PIDictionary = {
        var param = PIDictionary()

        let language = supportedLanguage
        param[Constants.country] = language.countryCode
        param[Constants.language] = language.rawValue

        return param
    }()

    func addLocalisationParameters(parameters: PIDictionary?) -> PIDictionary {
        var finalParameters = LanguageManager.localisationParameters

        parameters?.forEach({ (key: String, value: Any) in
            finalParameters[key] = value
        })

        return finalParameters
    }

    func formatter(currency: String?) -> NumberFormatter {
        if Locale.current.language.languageCode?.identifier == "en" || currency == "EUR" {
            // If English show all currency on left hand side OR if EUR currency use the users Locale which in German will put EUR currency symbol on right hand side
            let formatter = NumberFormatter.defaultCurrencyFormatter
            formatter.currencyCode = currency
            return formatter
        } else {
            // If not english and not EUR put symbol on left hand side by setting the Locale to English(German user looking at £).
            let formatter = NumberFormatter.leftHandCurrencyFormatter
            formatter.currencyCode = currency
            return formatter
        }
    }
}
