package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Payment actions - Rule 1: NO_PAYMENT for UK hotel with no guest pay and no routing")
    request {
        method 'GET'
        urlPath('/v1/payment-methods/payment-actions/BASKET_UK_NO_PAYMENT')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("response/get_paymentActions_rule1_noPayment_uk.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}

