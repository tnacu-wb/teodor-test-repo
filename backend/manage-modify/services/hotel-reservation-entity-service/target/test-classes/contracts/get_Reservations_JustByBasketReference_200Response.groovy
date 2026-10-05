package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get reservations just by basket reference integration test")
    request {
        method 'GET'
        urlPath('/v1/reservations/basket/TestId1234567') {
            queryParameters {
                parameter 'priceBreakdownNeeded': false
            }
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("get_Reservations_JustByBasketReference_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}