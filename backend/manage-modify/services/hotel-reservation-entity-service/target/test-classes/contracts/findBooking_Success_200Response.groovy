package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get reservations by external reference integration test")
    request {
        method 'GET'
        urlPath('/v1/reservations/find') {
            queryParameters {
                parameter 'resNo': 'ABCD123456'
                parameter 'lastName': 'Caesar'
                parameter 'arrivalDate': '2023-12-30'
                parameter 'country': 'gb'
                parameter 'language': 'en'
                parameter 'channel': 'PI'
                parameter 'subchannel': 'MOBILE'
            }
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("findBooking_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}