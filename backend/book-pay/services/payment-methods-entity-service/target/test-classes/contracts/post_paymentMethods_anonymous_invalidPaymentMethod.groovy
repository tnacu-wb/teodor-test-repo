package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Verifies that a user can select a valid payment method")
    request {
        method 'POST'
        urlPath('/v1/payment-methods')
        body('''
            {
              "basketReference" : "LONHOL5778172",
              "type" : "CARD",
              "selectedPaymentOption": "RESERVE_WITHOUT_CARD"
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 409
    }
}