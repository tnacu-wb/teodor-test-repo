package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Create new basket")
    request {
        method 'POST'
        urlPath('/v1/baskets')
        body('''
            {
                "hotelId": "LONEUS",
                "userId": "user id test"
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 201
        headers {
            header('Content-Type', 'application/json')
        }
    }
}