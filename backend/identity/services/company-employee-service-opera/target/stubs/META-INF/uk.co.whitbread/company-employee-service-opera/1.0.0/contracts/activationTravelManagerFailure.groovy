package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Travel manager's account activation should return error when activation if key not present")
    request {
        method 'POST'
        url '/companies/activation'
        headers {
            header('Content-Type', 'application/json')
            header('bookingChannel', 'CBT')
        }
    }

    response {
        status 400
        body (
                '''
                {
                    "code": "013",
                    "details": [
                        "Required request parameter 'activation-key' for method parameter type String is not present"
                    ]
                }
                '''
        )
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
