package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Update override reasons for a reservation integration test")
    request {
        method 'PUT'
        urlPath('/v1/reservations/overrideReasons')
        body('''
             {
              "basketReference": "TestId1234567",
              "hotelId": "HOTELTEST",
              "reasonCode": "ILL",
              "reasonName": "Illness",
              "callerName": "John Doe",
              "managerName": "James Bond"
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("update_reservationOverrideReasons_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}