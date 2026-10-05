package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Payment actions - Rule 2: AUTHORIZE_CARD for DE hotel with no guest pay and no routing")
    request {
        method 'GET'
        urlPath('/v1/payment-methods/payment-actions/BASKET_DE_AUTHORIZE')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("response/get_paymentActions_rule2_authorizeCard_de.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}

