package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("hotel-availability-controller")
    request {
        method 'GET'
        urlPath(value(consumer(regex('v1/hotels/[A-Z]{6}/availabilities')))) {
            queryParameters {
                parameter 'arrivalDate': "invalidDate"
                parameter 'departureDate': "2032-12-28"
                parameter 'roomTypes': "DB"
                parameter 'adultsNumber': 2
                parameter 'childrenNumber': 0
                parameter 'cotsRequired': false
                parameter 'channel': "PI"
                parameter 'subchannel': "MOBILE"
                parameter 'language': "EN"
            }
        }
    }

    response {
        status 400
    }
}