package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Search bookings - Not Found - integration test")
    request {
        method 'GET'
        urlPath('/v1/reservations/search') {
            queryParameters {
                parameter 'bookingReference': 'GAA8056793'
                parameter 'bookerLastName': 'Pop'
                parameter 'arrivalDate': '3023-01-23'
            }
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("searchBookings_NoResults_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}