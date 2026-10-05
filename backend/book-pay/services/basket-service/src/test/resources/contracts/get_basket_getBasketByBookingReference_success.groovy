package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get basket by booking reference.")
    request {
        method 'GET'
        urlPath('/v1/baskets?bookingReference=LONHOL5778172')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("response/get_basket_getBasketByBookingReference_success.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}