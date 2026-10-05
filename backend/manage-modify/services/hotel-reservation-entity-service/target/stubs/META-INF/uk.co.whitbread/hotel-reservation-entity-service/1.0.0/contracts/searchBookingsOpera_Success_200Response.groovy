package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Search bookings in Opera integration test")
    request {
        method 'GET'
        urlPath('/v1/reservations/search') {
            queryParameters {
                parameter 'bookingReference': 'GAA8056792'
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
        body(file("searchBookingsOpera_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}