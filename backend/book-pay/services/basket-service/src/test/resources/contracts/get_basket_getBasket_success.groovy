package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get basket by basket reference.")
    request {
        method 'GET'
        urlPath('/v1/baskets/LON-7b97e3f6-dce9-4572-b3e1-a5aa91ebc4b7')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("response/get_basket_getBasket_success.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}