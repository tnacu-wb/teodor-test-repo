package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Payment actions - Rule 3: CREDIT_CARD for UK hotel with guest pay only")
    request {
        method 'GET'
        urlPath('/v1/payment-methods/payment-actions/BASKET_UK_CREDIT_CARD')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("response/get_paymentActions_rule3_creditCard_uk.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}

