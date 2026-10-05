package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Cancel basket")
    request {
        method 'PUT'
        urlPath('/v1/baskets/LON-7b97e3f6-dce9-4572-b3e1-a5aa91ebc4b7/cancel')
        body('''
            {
                  "isFailed": true,
                  "sendEmail": false
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