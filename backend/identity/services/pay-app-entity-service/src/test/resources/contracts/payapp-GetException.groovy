package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("pay-app-controller")
    request {
        method 'POST'
        urlPath('/v1/pay-app/application/initialize')
        body('''
                {
                    "email": "john.doe@email.com"
                }
                ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 500
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
