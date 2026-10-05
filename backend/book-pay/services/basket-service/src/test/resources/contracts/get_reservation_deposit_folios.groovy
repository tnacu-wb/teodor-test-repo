package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get reservation deposit folios by reservation id.")
    request {
        method 'GET'
        urlPath('/v1/baskets/deposit-folios/123reservation')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("response/get_reservation_deposit_folios_success.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
