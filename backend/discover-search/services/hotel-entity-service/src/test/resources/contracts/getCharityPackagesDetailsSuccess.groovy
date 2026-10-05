package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    priority(1)
    description("Should return donation package details")
    request {
        method 'GET'
        urlPath('v1/hotels/DHAMME/packages/donations') {
            queryParameters {
                parameter 'packageCodes': "CHRTY3, CHRTY4"
            }
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("response/get_charity_pkg_details_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}