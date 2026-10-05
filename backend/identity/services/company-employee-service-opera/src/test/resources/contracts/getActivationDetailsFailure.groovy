package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Employee Activation Details should return error when activation key is not present")
    request {
        method 'GET'
        url '/companies/employees/activation-details'
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
