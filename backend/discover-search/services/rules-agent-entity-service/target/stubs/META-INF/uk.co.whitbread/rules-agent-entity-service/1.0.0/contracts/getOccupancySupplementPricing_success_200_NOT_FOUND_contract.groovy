package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("rules-agent-controller")
    priority(1)
    request {
        method 'GET'
        urlPath('/v1/rules/occupancy-supplement') {

            queryParameters {
                parameter 'hotelId': "MANOLD"
            }
        }
    }

    response {
        status 200
        body('''
            {
              "hotelId": "MANOLD",
              "pricing": 0.0
            }'''
        )
        headers {
            header('Content-Type', 'application/json')
        }
    }
}