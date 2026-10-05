package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Hotel information is retrieved from AEM")
    request {
        method 'GET'
        urlPath('/v1/content/hotels/LONEUS/information') {
            queryParameters {
                parameter 'country': 'gb'
                parameter 'language': 'en'
                parameter 'channel': 'DISTR'
            }
        }
    }

    response {
        status 200
        body(file("response/get_hotelInformation_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}