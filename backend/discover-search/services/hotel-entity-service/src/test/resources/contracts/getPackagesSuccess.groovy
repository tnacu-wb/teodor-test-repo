package contracts.availability

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    priority(1)
    description("Should return hotel packages")
    request {
        method 'GET'
        urlPath('v1/hotels/HOTELCODE4/packages') {
            queryParameters {
                parameter 'startDate': '2022-04-02'
                parameter 'endDate': '2022-04-03'
                parameter 'adultsNumber': '2'
                parameter 'childrenNumber': '0'
                parameter 'nightsNumber': '2'
            }
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }
    response {
        status 200
        body(file("response/getPackagesSuccess.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}