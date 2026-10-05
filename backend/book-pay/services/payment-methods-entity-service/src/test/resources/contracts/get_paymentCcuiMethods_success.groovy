package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Available payment methods for anonymous users do not contain saved cards")
    request {
        method 'GET'
        urlPath('/v1/payment-methods/ccui') {
            queryParameters {
                parameter 'basketReference': 'LONHOL5778172'
                parameter 'country': 'gb'
                parameter 'language': 'en'
                parameter 'userType': 'AGENT'
            }
        }
        headers {
            header('Content-Type', 'application/json')
            header('bookingChannel', 'WEB')
        }
    }

    response {
        status 200
        body(file("response/get_paymentCcuiMethods_success.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}