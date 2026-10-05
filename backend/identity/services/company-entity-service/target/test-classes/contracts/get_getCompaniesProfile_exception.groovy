package contracts.profile

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get companies profile integration test")
    request {
        method 'GET'
        urlPath('/v1/companies/profile') {
            queryParameters {
                parameter 'hotelId': 'MANOLD'
                parameter 'limit': '30'
            }
        }
        headers {
            header('Content-Type', 'application/json;charset=UTF-8')
        }
    }

    response {
        status 500
        headers {
            header('Content-Type', 'application/json')
        }
    }
}