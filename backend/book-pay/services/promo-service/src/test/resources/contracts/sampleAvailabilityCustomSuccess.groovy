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
                    "fromDate":"2017-02-20"
                }
                ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body('''
        {
          "responseData": [
                {
                    "hotelCode": "HOTEL_CODE_TTTGG",
                    "numberOfRooms": 2
                }
            ]
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}