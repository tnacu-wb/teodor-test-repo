package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Seo information is retrieved from AEM")
    request {
        method 'GET'
        urlPath('/v1/content/seo') {
            queryParameters {
                parameter 'hotelId': 'LONHOL'
                parameter 'page': 'HDP'
                parameter 'country': 'gb'
                parameter 'language': 'en'
            }
        }
    }

    response {
        status 200
        body(file("response/get_seoInformation_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}