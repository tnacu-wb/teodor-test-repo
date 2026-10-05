package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Payment methods for a hotel are retrieved correctly from AEM")
    request {
        method 'GET'
        urlPath('/v1/content/hotels/LONHOL/payment-information') {
            queryParameters {
                parameter 'country': 'gb'
                parameter 'language': 'en'
            }
        }
    }

    response {
        status 200
        body(file("response/get_hotelPaymentInformation_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}