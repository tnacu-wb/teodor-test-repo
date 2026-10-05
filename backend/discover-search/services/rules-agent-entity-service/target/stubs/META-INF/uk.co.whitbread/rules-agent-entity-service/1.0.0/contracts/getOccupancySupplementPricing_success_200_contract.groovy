package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("rules-agent-controller")
    priority(1)
    request {
        method 'GET'
        urlPath('/v1/rules/occupancy-supplement') {

            queryParameters {
                parameter 'hotelId': "FRAMTI"
            }
        }
    }

    response {
        status 200
        body('''
            {
              "hotelId": "FRAMTI",
              "pricing": 9.99
            }'''
        )
        headers {
            header('Content-Type', 'application/json')
        }
    }
}