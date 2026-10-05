package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("cookie-policies-controller")
    priority(1)
    request {
        method 'GET'
        urlPath('/v1/content/cookie-policies') {

            queryParameters {
                parameter 'country': 'gb'
                parameter 'language': 'en'
                parameter 'brand': 'pi'
            }
        }
    }

    response {
        status 200
        body(file("response/get_cookie_policies_success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}