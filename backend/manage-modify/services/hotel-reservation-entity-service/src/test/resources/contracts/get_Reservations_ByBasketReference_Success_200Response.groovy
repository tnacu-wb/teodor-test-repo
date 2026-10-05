package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get reservations by basket reference integration test")
    request {
        method 'GET'
        urlPath('/v1/reservations') {
            queryParameters {
                parameter 'hotelId': 'TESTHOTEL'
                parameter 'basketReference': 'TestId1234567'
                parameter 'limit': '20'
                parameter 'offset': '0'
            }
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("get_Reservations_ByBasketReference_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}