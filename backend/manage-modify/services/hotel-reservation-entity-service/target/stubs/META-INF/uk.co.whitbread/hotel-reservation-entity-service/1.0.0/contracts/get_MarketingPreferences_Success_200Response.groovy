package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get reservations packages by basket reference integration test")
    request {
        method 'GET'
        urlPath('/v1/reservations/marketingPreferences') {
            queryParameters {
                parameter 'hotelId': 'MANOLD'
                parameter 'reservationId': '853004'
            }
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("get_MarketingPreferences_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}