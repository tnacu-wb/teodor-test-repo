//
//  CiolBackgroundChargeQuery.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 29/06/2026.
//

import Foundation

extension GraphQL {
    static let ciolBackgroundChargeQuery =
    """
    mutation BackgroundCharge($basketReference: String!, $token: String!) {
      backgroundCharge(basketReference: $basketReference, token: $token) {
        basket {
          basketReference
        }
      }
    }
    """
}
