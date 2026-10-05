//
//  MarketingQuery.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 02/05/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static let getMarketingPermissionsQuery =
    """
    query getContactPreferences($request: PreferencesGetRequest!) {
        getContactPreferences(request: $request) {
            permissions{
                brandCode
                optIn
                suppressMarketingCheckbox
            }
            contactChannelId
        }
    }
    """

    static let updateMarketingPermissionsMutation =
    """
    mutation updateMarketingPreferences (
        $brandCodes: [String!]
        $optIn: Boolean!
        $doubleOptIn: Boolean!
        $customer: CustomerDetails!
        $sourceDetails: SourceDetails!
    ) {
        updateMarketingPreferences(
          updateMarketingPreferencesRequest: {
            brandCodes: $brandCodes,
            optIn: $optIn,
            doubleOptIn: $doubleOptIn,
            customer: $customer,
            sourceDetails: $sourceDetails
          }
        )
    }
    """
}
