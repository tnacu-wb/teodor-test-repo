package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("rate-entity-controller")
    request {
        method 'GET'
        urlPath('/v1/content/booking/rateInformation') {
            queryParameters {
                parameter 'country': 'gb'
                parameter 'language': 'en'
                parameter 'brand': 'pi'
                parameter 'hotelId': 'LONEUS'
                parameter 'channel': 'PI'
                parameter 'ratePlans': ['FLEXRATE'].join()
            }
        }
    }

    response {
        status 200
        body(file("response/get_rateInformation_success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}