package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Update discount for a reservation integration test")
    request {
        method 'PUT'
        urlPath('/v1/reservations/discount')
        body('''
             {
              "reservationIds": ["12345", "123457"],
              "discountAmount": "10",
              "currency": "EUR",
              "hotelId": "HOTELTEST"
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
    }
}