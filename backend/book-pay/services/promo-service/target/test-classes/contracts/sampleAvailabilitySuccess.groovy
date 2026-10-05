package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("sample-availability-controller")
    request {
        method 'GET'
        urlPath('/v1/sample/hotels/availabilities') {
        }
    }

    response {
        status 200
        body('''
        {
            "responseData":[{"hotelName":"HOTEL_CODE_TTTGG"}]
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}