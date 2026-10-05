package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Getting search rules, which is retrieved from AEM (channelId: ccui, brand: PI)")
    request {
        method 'GET'
        urlPath('/v1/content/searchrules') {
            queryParameters {
                parameter 'channelId': 'ccui'
                parameter 'brand': 'PI'
            }
        }
    }

    response {
        status 200
        body(file("response/get_search_rules_channel_CCUI_brand_PI_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}