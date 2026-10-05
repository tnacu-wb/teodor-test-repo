package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Amend flow - Amend payment page")
    request {
        method 'GET'
        urlPath('/v1/reservations/amend/paymentOptions') {
            queryParameters {
                parameter 'originalBookingRef': 'AQN-6af52126-37b6-434f-8ab4-e8be7b7d52d0'
                parameter 'tempBookingRef': 'AKU-276afd23-b1fd-4cbc-96f1-d0a1f814d4e8'
                parameter 'token': '1234567890'
                parameter 'bookingChannel.channel': 'CCUI'
                parameter 'bookingChannel.subchannel': 'WEB'
                parameter 'bookingChannel.language': 'EN'
                parameter 'country': 'gb'
            }
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("amendPaymentPage_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
