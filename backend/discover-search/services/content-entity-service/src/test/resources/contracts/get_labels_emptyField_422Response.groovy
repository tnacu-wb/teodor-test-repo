package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("content-entity-controller-exception")
    priority(100)
    request {
        method 'GET'
        urlPath('/v1/content/labels/main') {
            queryParameters {
                parameter 'country': 'en'
                parameter 'language': ''
            }
        }

    }

    response {
        status 422
        body(file("response/get_labels_emptyField_422Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}