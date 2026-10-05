//
//  CiolPaymentActionsResponse.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 26/05/2026.
//

import Foundation

// MARK: - CiolPaymentActionsResponse

public struct CiolPaymentActionsResponse: Decodable {
    public let displayPaymentPage: Bool?
    public let paymentActions: [PaymentAction]?

    public init(displayPaymentPage: Bool?, paymentActions: [PaymentAction]?) {
        self.displayPaymentPage = displayPaymentPage
        self.paymentActions = paymentActions
    }
}

// MARK: - PaymentAction

public extension CiolPaymentActionsResponse {
    struct PaymentAction: Decodable {
        public let chargeType: ChargeType?
        public let price: Cost?

        public init(chargeType: ChargeType?, price: Cost?) {
            self.chargeType = chargeType
            self.price = price
        }
    }
}

// MARK: - ChargeType

public extension CiolPaymentActionsResponse.PaymentAction {
    enum ChargeType: String, Decodable {
        case creditCard = "CREDIT_CARD"
        case cardOnFile = "CARD_ON_FILE"
        case authorizeCard = "AUTHORIZE_CARD"
        case noPayment = "NO_PAYMENT"
    }
}
