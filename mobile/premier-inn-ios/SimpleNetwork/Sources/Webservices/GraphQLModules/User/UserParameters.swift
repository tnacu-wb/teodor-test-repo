//
//  UserParameters.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 02/12/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static func getInitiateSaveCardVariables(
        initiateSaveCardParameters: InitiateSaveCardParameters,
        environment: String
    ) throws -> PIDictionary {
        var 📂 = PIDictionary()

        let uuid = UUID().uuidString
        📂[.requestId] = uuid
        📂["billingAddress"] = getAddress(initiateSaveCardParameters.billingAddress)
        📂["cardDetails"] = getCardDetails(initiateSaveCardParameters.cardDetails)
        📂["environment"] = environment
        // Send the iso code for the device language to show the correct currency
        📂["country"] = LanguageManager.supportedLanguage.countryCode.uppercased()
        📂["language"] = LanguageManager.supportedLanguage.rawValue

        return ["initiateSaveCardRequest": 📂]
    }

    private static func getAddress(_ address: Address) -> PIDictionary {
//        let city = (address.line2?.isEmpty == false ? address.line2 : address.line1) ?? ""
        let country = address.country?.isoCode ?? ""

        var addressDictionary: PIDictionary = [
            "line1": address.line1 ?? "",
            "line2": address.line2 ?? "",
            "line3": address.line3 ?? "",
            "line4": address.line4 ?? "",
            "postCode": address.postcode ?? "",
//            "city": city,
            "countryCode": country,
            "type": (address.type ?? AddressType.home).rawValue
        ]

        if let companyName = address.companyName {
            addressDictionary["companyName"] = companyName
        }

        return addressDictionary
    }

    private static func getCardDetails(_ cardDetails: InitiateSaveCardDetails) -> PIDictionary {
        var cardDetailsDictionary: PIDictionary = [
            "cardType": cardDetails.cardType.rawValue,
            "cnpRequired": cardDetails.cnpRequired
        ]

        if let memorableWord = cardDetails.memorableWord {
            cardDetailsDictionary["memorableWord"] = memorableWord
        }

        return cardDetailsDictionary
    }

    static func forgotPasswordVariables(isBusiness: Bool, email: String) -> PIDictionary {
        var dict = PIDictionary()
        dict["language"] = LanguageManager.supportedLanguage.rawValue
        dict["innBusiness"] = isBusiness
        dict["forgottenPasswordRequest"] = ["username": email]

        return dict
    }

    static func createAccountVariables(registerParameters: RegisterParameters) -> PIDictionary {
        var dict = PIDictionary()
        dict["language"] = LanguageManager.supportedLanguage.rawValue
        dict["country"] = LanguageManager.supportedLanguage.countryCode.uppercased()
        dict["password"] = registerParameters.password
        dict["contactDetail"] = contactDetailDictionary(user: registerParameters.user)
        dict["updatePreferencesRequest"] = marketingPreferencesDictionary(registerParameters: registerParameters)

        return ["createAccountRequest": dict]
    }

    static func marketingPreferencesDictionary(registerParameters: RegisterParameters) -> PIDictionary {
        var dict = PIDictionary()

        dict["optIn"] = registerParameters.marketingOptIn
        dict["doubleOptIn"] = registerParameters.doubleOptIn
        dict["brandCodes"] = [registerParameters.brandCodes.first?.rawValue ?? ""]
        dict["customer"] = [
            "language": LanguageManager.supportedLanguage.rawValue,
            "countryOfResidence": registerParameters.isoCountryCode,
            "firstName": registerParameters.user.firstName,
            "lastName": registerParameters.user.lastName,
            "title": registerParameters.user.title
        ]
        dict["sourceDetails"] = [
            "channel": Constants.CCC.channelIOS,
            "journey": Constants.emailJourney,
            "locale": LanguageManager.supportedLanguage.analyticsCode
        ]

        return dict
    }

    static func contactDetailDictionary(user: User) -> PIDictionary {
        var dict = PIDictionary()

        dict["title"] = user.title
        dict["firstName"] = user.firstName
        dict["lastName"] = user.lastName
        dict["mobile"] = user.contactNumber
        dict["emailAddress"] = user.emailAddress
        dict["address"] = getGuestAddress(user: user)

        return dict
    }
}
