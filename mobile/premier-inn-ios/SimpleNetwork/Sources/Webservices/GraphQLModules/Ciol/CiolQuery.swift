//
//  CIOLQuery.swift
//  SimpleNetwork
//
//  Created by Muresan, Andreea (Cognizant) on 16.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static func confirmPreCheckInOutQuery(resolver: String, includeIsCiol: Bool) -> String {
        if includeIsCiol {
            // For check-in: include isCiol parameter
            return """
            mutation confirmPreCheckInOut(
              $basketReference: String!
              $isCiol: Boolean
            ) {
              \(resolver)(
                basketReference: $basketReference
                isCiol: $isCiol
              ) {
                basketReference
                basketStatus
                basketError {
                    code
                    description
                    type
                }
              }
            }
            """
        } else {
            // For check-out: exclude isCiol parameter
            return """
            mutation confirmPreCheckInOut(
              $basketReference: String!
            ) {
              \(resolver)(
                basketReference: $basketReference
              ) {
                basketReference
                basketStatus
                basketError {
                    code
                    description
                    type
                }
              }
            }
            """
        }
    }
}
