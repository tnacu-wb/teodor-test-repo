package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get reviews for Single Hotel")
    request {
        method 'GET'
        urlPath('/v1/hotel-review/reviews/KINPTI')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        headers {
            header('Content-Type', 'application/json')
        }
    }
}