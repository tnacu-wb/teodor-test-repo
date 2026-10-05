package contracts
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get hotel preferences for a group")
    request {
        method 'GET'
        urlPath('v1/hotels/TestHotelId/preferences') {
            queryParameters {
                parameter 'preferenceGroupsCodes': "testGroupCode"
            }
        }
        headers {
            header('Content-Type', 'application/json;charset=UTF-8')
        }
    }

    response {
        status 200
        body('''
        {
            "hotelPreferences": [
                {
                  "hotelId": "TestHotelId",
                  "description": "Chocolate Box",
                  "code": "BIRTHDAY",
                  "preferenceGroup": "EVENTS",
                  "orderSequence": "2"
                },
                {
                  "hotelId": "TestHotelId",
                  "description": "Dietary",
                  "code": "DIETARY",
                  "preferenceGroup": "EVENTS",
                  "orderSequence": "2"
                }
          ]
  
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}