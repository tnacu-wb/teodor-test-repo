package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Getting index.header.data, which is retrieved from AEM")
    request {
        method 'GET'
        urlPath('/v1/content/header/data') {
            queryParameters {
                parameter 'country': 'gb'
                parameter 'language': 'en'
            }
        }
    }

    response {
        status 200
        body(file("response/get_header_data_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}