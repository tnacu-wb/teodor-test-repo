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
                parameter 'language': 'badInput'
            }
        }

    }

    response {
        status 500
        body(file("response/get_labels_badLanguage_500Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}