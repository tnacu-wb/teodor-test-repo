package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Hotel information is retrieved from AEM")
    request {
        method 'GET'
        urlPath('/v1/content/businessNotes') {
            queryParameters {
                parameter 'lang': 'en'
            }
        }
    }

    response {
        status 200
        body(file("response/get_bookingNotes_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}