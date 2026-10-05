//
//  CiolPaymentActionsQuery.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 26/05/2026.
//

import Foundation

extension GraphQL {
   static let ciolPaymentActionsQuery =
   """
   query checkInOnlinePaymentActions($basketReference: String!) {
    checkInOnlinePaymentActions(basketReference: $basketReference) {
       displayPaymentPage
       paymentActions {
         chargeType
         price {
           amount
           currency
         }
       }
    }
   }
   """
}
