package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("sample-availability-controller")
    request {
        method 'POST'
        urlPath('/v1/sample/hotels/availabilities/custom')
        body('''
                {
                    "hotelName":"123",
                    "fromDate":"xx"
                }
                ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 400
        body('''
        {
            "code": "001",
            "details": [
                "fromDate must be in correct date format"
            ]
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}