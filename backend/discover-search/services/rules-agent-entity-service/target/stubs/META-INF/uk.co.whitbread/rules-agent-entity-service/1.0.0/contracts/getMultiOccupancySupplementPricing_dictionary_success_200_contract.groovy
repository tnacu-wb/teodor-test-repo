package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("rules-agent-controller")
    priority(1)
    request {
        method 'POST'
        headers {
            header('Content-Type', 'application/json')
        }
        urlPath('/v1/rules/multi-occupancy-supplement')
        body('''
        {
            "hotelIds": [{
                "hotelId": "FRAMTI"
            }]
        }
        ''')
    }

    response {
        status 200
        body('''
            {
              "dictionary": {
                "FRAMTI": 9.99
              }
            }'''
        )
        headers {
            header('Content-Type', 'application/json')
        }
    }
}