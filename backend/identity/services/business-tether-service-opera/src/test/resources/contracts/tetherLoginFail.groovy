package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Tether Login Request Failure")
    request {
        method 'POST'
        url '/business/tether/login'
        body (
                '''
                {
                  "guid": "2458b429-bf1f-495d-9453-cc41d8070244"
                }
            '''
        )
        headers {
            header('Content-Type', 'application/json;charset=UTF-8')
            header("Authorization", "Bearer dummy_value")
        }
    }

    response {
        status 500
    }
}