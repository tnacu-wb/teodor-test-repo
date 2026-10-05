//
//  PostcodeLookupParameters.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static func partialAddressVariables(postCode: String) throws -> PIDictionary {
       var dict = PIDictionary()

       dict["searchTerm"] = postCode
       // Unused countryCode field as not required. Maybe used in future for different countries postcode styles
       // dict["countryCode"] = GB
       return dict
    }

    static func formattedAddressVariables(id: String) throws -> PIDictionary {
       var dict = PIDictionary()

       dict["identifier"] = id
       return dict
    }
}
