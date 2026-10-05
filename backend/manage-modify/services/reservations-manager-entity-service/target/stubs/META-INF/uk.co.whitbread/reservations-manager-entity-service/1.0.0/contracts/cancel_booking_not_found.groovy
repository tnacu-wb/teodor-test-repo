package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("reservations-manager-entity-controller")
    request {
        method 'POST'
        headers {
            header('Content-Type', 'application/json')
            header('WB-Authorization', 'Bearer test==')
        }
        urlPath('/v1/bookings/cancel')
        body('''{
                "hotelId": "DASDAD",
                "bookingReference": "bookingReference",
                "arrivalDate": "2023-03-10",
                "sourceSystem": "BART"
            }''')
    }
    response {
        status 500
    }
}