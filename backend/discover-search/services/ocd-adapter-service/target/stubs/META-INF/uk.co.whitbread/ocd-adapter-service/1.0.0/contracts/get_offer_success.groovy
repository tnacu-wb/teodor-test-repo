package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get hotel offer integration test")
    request {
        method 'GET'
        urlPath('/v1/hotels/BERCIT/offer') {
            queryParameters {
                parameter 'arrivalDate': '9999-10-01'
                parameter 'departureDate': '9999-10-02'
                parameter 'roomType': 'DOUBLE'
                parameter 'adults': '1'
                parameter 'ratePlanCode': "FLEXRATE"
            }
        }
        headers {
            header('Content-Type', 'application/json;charset=UTF-8')
        }
    }

    response {
        status 200
        body(file("response/get_offer_success.json")
        )
        headers {
            header('Content-Type', 'application/json')
        }
    }
}