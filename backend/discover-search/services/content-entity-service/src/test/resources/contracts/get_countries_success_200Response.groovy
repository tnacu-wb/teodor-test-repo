import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Countries information is retrieved from AEM")
    request {
        method 'GET'
        urlPath('/v1/content/countries') {

            queryParameters {
                parameter 'country': 'gb'
                parameter 'language': 'en'
                parameter 'site': 'leisure'
            }
        }
    }

    response {
        status 200
        body(file("response/get_countries_success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}