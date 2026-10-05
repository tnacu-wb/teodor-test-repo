package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Available payment methods for a leisure user contain saved cards")
    request {
        method 'GET'
        urlPath('/v1/payment-methods') {
            queryParameters {
                parameter 'basketReference': 'LONHOL5778172'
                parameter 'country': 'gb'
                parameter 'language': 'en'
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
        body(file("response/get_paymentMethods_businessUserSavedCards_success.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}