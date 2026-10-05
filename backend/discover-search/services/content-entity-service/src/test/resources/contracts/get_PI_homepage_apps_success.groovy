package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Getting homepage's elements from AEM")
    request {
        method 'GET'
        urlPath('/v1/content/homepage') {
            queryParameters {
                parameter 'country': 'gb'
                parameter 'language': 'en'
                parameter 'channel': 'PI'
                parameter 'subchannel': 'apps'
            }
        }
    }

    response {
        status 200
        body(file("response/get_PI_homepage_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}