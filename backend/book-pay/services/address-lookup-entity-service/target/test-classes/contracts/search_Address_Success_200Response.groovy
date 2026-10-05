package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Search addresses by postcode integration test")
    request {
        method 'GET'
        urlPath('/v1/addresses') {
            queryParameters {
                parameter 'searchTerm': 'EC1N2TD'
            }
        }
    }

    response {
        status 200
        body(file("response/search_Address_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}