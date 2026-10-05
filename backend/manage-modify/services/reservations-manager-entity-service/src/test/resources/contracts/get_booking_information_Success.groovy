package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("reservations-manager-entity-controller")
    request {
        method 'GET'
        headers {
            header('Content-Type', 'application/json')
        }
        urlPath('/v1/bookings/information') {
            queryParameters {
                parameter 'hotelId': 'DUBAIR'
                parameter 'bookingReference': 'AWQ9273512'
                parameter 'arrival': '2020-02-05'
                parameter 'country': 'gb'
                parameter 'language': 'en'
                parameter 'surname': 'ln'
                parameter 'channel': 'PI'
                parameter 'subchannel': 'WEB'
                parameter 'token': 'token'
            }
        }
    }
    response {
        status 200
        body(file("response/information/get_booking_information_success.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}