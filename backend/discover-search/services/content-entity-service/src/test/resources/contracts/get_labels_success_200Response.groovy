package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("content-entity-controller")
    priority(1)
    request {
        method 'GET'
        urlPath('/v1/content/labels/main') {

            queryParameters {
                parameter 'country': 'en'
                parameter 'language': 'en'
            }
        }
    }

    response {
        status 200
        body(file("response/get_labels_success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}