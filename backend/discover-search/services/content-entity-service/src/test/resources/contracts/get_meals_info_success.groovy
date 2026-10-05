package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("meals-entity-controller")
    request {
        method 'GET'
        urlPath('/v1/content/meals') {
            queryParameters {
                parameter 'country': 'gb'
                parameter 'language': 'en'
                parameter 'hotelId': 'MUNCIT'
            }
        }
    }

    response {
        status 200
        body(file("response/get_meals_success_200Response.json"))
        bodyMatchers {
            jsonPath("upsellItems[*].attachments", byType {
                minOccurrence(1)
            })
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }
}