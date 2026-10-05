package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("content-entity-controller-exception")
    priority(100)
    request {
        method 'GET'
        urlPath('/v1/content/booking') {
            queryParameters {
                parameter 'country': 'gb'
                parameter 'language': 'en'
                parameter 'hotelId': 'MANOLD'
                parameter 'bookingFlowId': ''
            }
        }

    }

    response {
        status 422
        body(file("response/get_booking_information_422Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}