package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Hotel information is retrieved from AEM")
    request {
        method 'GET'
        urlPath('/v1/content/hotels/information') {
            queryParameters {
                parameter 'hotelIds': 'LONLEI,LONEUS'
                parameter 'country': 'de'
                parameter 'language': 'de'
            }
        }
    }

    response {
        status 200
        body(file("response/get_hotelsInformation_de_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}