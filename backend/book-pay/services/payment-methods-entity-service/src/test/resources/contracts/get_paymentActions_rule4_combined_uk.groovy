package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Payment actions - Rule 4: CARD_ON_FILE + CREDIT_CARD for UK hotel with both guest pay and routing")
    request {
        method 'GET'
        urlPath('/v1/payment-methods/payment-actions/BASKET_UK_COMBINED')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("response/get_paymentActions_rule4_combined_uk.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}

