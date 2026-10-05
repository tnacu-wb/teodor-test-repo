package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Payment actions - Rule 5: CARD_ON_FILE for UK hotel with two reservations, both with routing only (aggregated)")
    request {
        method 'GET'
        urlPath('/v1/payment-methods/payment-actions/BASKET_UK_MULTI_ROOM_CARD_ON_FILE')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("response/get_paymentActions_rule5_multiRoom_cardOnFile_uk.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}

