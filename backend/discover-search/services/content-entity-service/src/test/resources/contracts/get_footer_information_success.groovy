package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Footer information is retrieved from AEM")
    request {
        method 'GET'
        urlPath('/v1/content/footer') {
            queryParameters {
                parameter 'country': 'gb'
                parameter 'language': 'en'
                parameter 'site': 'leisure'
            }
        }
    }

    response {
        status 200
        body(file("response/get_footerInformation_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}