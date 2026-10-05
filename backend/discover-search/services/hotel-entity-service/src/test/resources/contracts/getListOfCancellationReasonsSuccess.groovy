package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get list of cancellation reasons integration test")
    request {
        method 'GET'
        urlPath('v1/hotels/HOTELCODE/cancellationReasons') {
            queryParameters {
                parameter 'hotelId': "HOTELCODE"
            }
        }
    }

    response {
        status 200
        body(file("response/get_list_of_cancellation_reasons_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
