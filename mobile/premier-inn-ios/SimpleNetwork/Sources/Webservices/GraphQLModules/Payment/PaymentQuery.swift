//
//  PaymentQuery.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static let initiatePaymentMutation =
    """
    mutation initiatePayment ($basketReference: String!, $createPaymentCriteria: CreatePaymentCriteria!) {
        initiatePayment(basketReference: $basketReference, createPaymentCriteria: $createPaymentCriteria) {
            status
            paymentRequiredDetails {
                paymentRedirect
            }
        }
    }
    """

    static let initiatePayPalPaymentMutation =
    """
    mutation initiatePaypalPayment ($basketReference: String!, $createPaymentCriteria: CreatePaymentCriteria!) {
            initiatePaypalPayment(basketReference: $basketReference, createPaymentCriteria: $createPaymentCriteria) {
            status
            paymentRequiredDetails {
                paymentRedirect
            }
        }
    }
    """

    static let paymentMethodsQuery =
    """
    query paymentMethods($paymentMethodsCriteria: PaymentMethodsCriteria!) {
        paymentMethods(paymentMethodsCriteria: $paymentMethodsCriteria) {
            name
            type
            logoSrc
            order
            enabled
            clientToken
            cnpOptionAvailable
            acceptedCardTypes {
                type
                logoSrc
                name
            }
            card {
                token
                expiryMonth
                expiryYear
                type
                logoSrc
                cardHolderName
                cardType
                cnpRequired
            }
            paymentOptions {
                type
                enabled
            }
            reasons
            subType
        }
    }
    """
}
