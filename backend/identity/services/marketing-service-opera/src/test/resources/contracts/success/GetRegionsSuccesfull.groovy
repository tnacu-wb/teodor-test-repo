package contracts.success

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Should get regions")
    request {
        method 'GET'
        url '/marketing/hotels/regions'
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body('''
        {
           "regions": [
            {
              "id": "1",
              "description": "UK & Ireland",
              "active": true
            },
            {
              "id": "2",
              "description": "Dubai",
              "active": true
            },
            {
              "id": "3",
              "description": "India",
              "active": true
            },
            {
              "id": "4",
              "description": "hub by Premier Inn",
              "active": true
            },
            {
              "id": "5",
              "description": "Germany",
              "active": true
            }
          ]
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}