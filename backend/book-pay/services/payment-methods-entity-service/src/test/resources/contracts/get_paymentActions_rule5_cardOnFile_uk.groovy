package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Payment actions - Rule 5: CARD_ON_FILE for UK hotel with routing only")
    request {
        method 'GET'
        urlPath('/v1/payment-methods/payment-actions/BASKET_UK_CARD_ON_FILE')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("response/get_paymentActions_rule5_cardOnFile_uk.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}

