package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get rate code pricing")
    request {
        method 'GET'
        urlPath('v1/hotels/TESTER/rate-code-pricing') {
            queryParameters {
                parameter 'arrivalDate': "2022-12-20"
                parameter 'departureDate': "2022-12-28"
                parameter 'ratePlanCode': "FLEXRATE"
                parameter 'roomTypes': "DOUBLE,DOUBLE"
                parameter 'adultsNo': "1,1"
                parameter 'childrenNo': "1"
            }

        }
    }

    response {
        status 200
        body(file("response/getRateCodePricingSuccess.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}