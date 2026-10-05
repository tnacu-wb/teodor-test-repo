package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Hotel information by slug is retrieved from AEM")
    request {
        method 'GET'
        urlPath('/v1/content/hotels') {
            queryParameters {
                parameter 'slug': '/hotels/england/greater-london/london/london-euston.html'
                parameter 'country': 'gb'
                parameter 'language': 'en'
            }
        }
    }

    response {
        status 200
        body(file("response/get_hotelInformation_by_slug_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}