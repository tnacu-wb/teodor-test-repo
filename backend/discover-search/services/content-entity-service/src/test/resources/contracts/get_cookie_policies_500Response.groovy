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
                parameter 'language': 'badInput'
                parameter 'brand': 'pi'
            }
        }
    }

    response {
        status 500
        body(file("response/get_cookie_policies_500Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}