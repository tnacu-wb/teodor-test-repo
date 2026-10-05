package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get hotel rooms inventory integration test")
    request {
        method 'GET'
        urlPath('v1/hotels/HOTELCODE/hotelInventory') {
            queryParameters {
                parameter 'dateRangeStart': '2032-03-01'
                parameter 'dateRangeEnd': '2032-03-03'
            }
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("response/get_HotelInventory_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}