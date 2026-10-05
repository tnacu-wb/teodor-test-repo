package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Delete basket")
    request {
        method 'DELETE'
        urlPath('/v1/baskets/LON-7b97e3f6-dce9-4572-b3e1-a5aa91ebc4b7')
        headers {
            header('Content-Type', 'application/json')
            header('If-Match', "1684426938941")
        }
    }

    response {
        status 204
    }
}