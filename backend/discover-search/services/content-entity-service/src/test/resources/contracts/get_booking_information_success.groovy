package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Booking information is retrieved from AEM")
    request {
        method 'GET'
        urlPath('/v1/content/booking') {
            queryParameters {
                parameter 'country': 'gb'
                parameter 'language': 'en'
                parameter 'hotelId': 'DUBSOU'
                parameter 'bookingFlowId': 'booking-a1'
            }
        }
    }

    response {
        status 200
        body(file("response/get_bookingInformation_200Response.json"))
        bodyMatchers {
            jsonPath('promotionPanels[*].displayHotels', byType { minOccurrence(0) })
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }
}