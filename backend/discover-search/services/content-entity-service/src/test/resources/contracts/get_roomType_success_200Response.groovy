package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("content-entity-controller")
    priority(1)
    request {
        method 'GET'
        urlPath('/v1/content/room-type') {

            queryParameters {
                parameter 'country': 'gb'
                parameter 'language': 'en'
                parameter 'brand': 'pi'
            }
        }
    }

    response {
        status 200
        body(file("response/get_roomType_success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}