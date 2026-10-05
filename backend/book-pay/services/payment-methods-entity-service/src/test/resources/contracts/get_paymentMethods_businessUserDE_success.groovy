package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Available payment methods for a leisure user and language=DE do contain saved cards")
    request {
        method 'GET'
        urlPath('/v1/payment-methods') {
            queryParameters {
                parameter 'basketReference': 'MUNCIT5778172'
                parameter 'country': 'de'
                parameter 'language': 'de'
                parameter 'userType': 'BUSINESS'
            }
        }
        headers {
            header('Content-Type', 'application/json')
            header('WB-Authorization', 'Bearer test==')
            header('bookingChannel', 'WEB')
        }
    }

    response {
        status 200
        body(file("response/get_paymentMethods_businessUserNoSavedCardsDE_success.json"))
        bodyMatchers {
            jsonPath('$[0].reasons', byType { minOccurrence(0) })
            jsonPath('$[1].acceptedCardTypes', byType { minOccurrence(0) })
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }
}