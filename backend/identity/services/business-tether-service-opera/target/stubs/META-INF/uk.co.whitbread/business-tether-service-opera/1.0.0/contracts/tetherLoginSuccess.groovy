package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Tether Login Request Success")
    request {
        method 'POST'
        url '/business/tether/login'
        body (
                '''
                {
                  "guid": "cdac7548-467b-4fbd-85bb-2803a4d38288"
                }
            '''
        )
        headers {
            header('Content-Type', 'application/json;charset=UTF-8')
            header("Authorization", "Bearer dummy_value")
        }
    }

    response {
        status 201
    }
}