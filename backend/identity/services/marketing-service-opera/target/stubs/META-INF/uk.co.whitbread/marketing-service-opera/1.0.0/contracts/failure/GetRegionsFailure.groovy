package contracts.failure

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Should fail getting regions")
    request {
        method 'GET'
        url '/marketing/hotels/regions'
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 400
        body('''
           {
              "code": "023",
              "details": [
                "Bart returned empty response."
              ]
            }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}