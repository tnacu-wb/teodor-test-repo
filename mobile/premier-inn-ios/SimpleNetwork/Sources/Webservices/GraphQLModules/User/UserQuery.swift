//
//  UserQuery.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 02/12/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static let initiateSaveCardMutation =
    """
    mutation saveCard($initiateSaveCardRequest: InitiateSaveCardRequest!) {
        saveCard(initiateSaveCardRequest: $initiateSaveCardRequest) {
            paymentRedirect
            template
            sessionId
            providerUrl
        }
    }
    """

    static let anonNewsLetterPreferences =
    """
    query anonymousNewsletterPreferences($email: String!, $brandCode: String!, $countryOfResidence: String!, $language: String!) {
        anonymousNewsletterPreferences(email: $email, brandCode: $brandCode, countryOfResidence: $countryOfResidence, language: $language) {
            optIn
            suppressMarketingCheckbox
        }
    }
    """

    static let forgotPasswordMutation =
    """
    mutation forgotPassword($language: String, $innBusiness: Boolean, $forgottenPasswordRequest: ForgottenPasswordRequest!) {
        forgotPassword(language: $language, innBusiness: $innBusiness, forgottenPasswordRequest: $forgottenPasswordRequest) {
            success
        }
    }
    """

    static let createAccountMutation =
    """
    mutation createAccount($createAccountRequest: CreateAccountRequest!) {
        createAccount(createAccountRequest: $createAccountRequest) {
            success
            customerId
        }
    }
    """
}
