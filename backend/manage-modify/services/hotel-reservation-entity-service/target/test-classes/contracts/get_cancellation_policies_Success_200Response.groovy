package contracts.ohip

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    priority(1)
    description("Get cancellation policies by external reference ids integration test")
    request {
        method 'GET'
        urlPath('/v1/reservations/cancellationPolicies') {
            queryParameters {
                parameter 'hotelId': 'HOTELTEST'
                parameter 'basketReference': 'basketCancellation'
                parameter 'ratePlanCode': ''
                parameter 'arrivalDate': ''
            }
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("get_cancellation_policies_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}