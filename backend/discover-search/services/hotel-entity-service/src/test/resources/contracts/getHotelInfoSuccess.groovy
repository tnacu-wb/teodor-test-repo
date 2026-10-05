package contracts
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get hotel configuration integration test")
    request {
        method 'GET'
        urlPath('v1/hotels/TestHotelId/info')
        headers {
            header('Content-Type', 'application/json;charset=UTF-8')
        }
    }

    response {
        status 200
        body('''
        {
            "threeLetterId": "ABC"
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}