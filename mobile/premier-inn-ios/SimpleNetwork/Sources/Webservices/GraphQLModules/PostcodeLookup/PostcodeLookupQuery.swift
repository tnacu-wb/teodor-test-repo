//
//  PostcodeLookupQuery.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static let addressLookUpQuery =
    """
    query getAddressListByPostcode($searchTerm: String!, $countryCode: String) {
      partialAddress(
        partialAddressCriteria: {
          searchTerm: $searchTerm
          countryCode: $countryCode
        }
      ) {
        id
        addressText
      }
    }
    """

    static let formattedAddressQuery =
    """
    query getAddressDetails($identifier: String!) {
      formattedAddress(identifier: $identifier) {
        companyName
        addressLine1
        addressLine2
        addressLine3
        addressLine4
        companyName
        label
        postalCode
        country
      }
    }
    """
}
