package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Verifies that a user is forbidden from selecting invalid payment methods")
    request {
        method 'POST'
        urlPath('/v1/payment-methods')
        body('''
            {
              "basketReference" : "LONHOL5778172",
              "type": "CARD",
              "selectedPaymentOption": "PAY_ON_ARRIVAL"
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
    }
}