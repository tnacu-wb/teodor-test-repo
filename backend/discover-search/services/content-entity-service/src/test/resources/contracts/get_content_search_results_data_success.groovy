package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Getting content search results data, which is retrieved from AEM")
    request {
        method 'GET'
        urlPath('/v1/content/searchresults/data') {
            queryParameters {
                parameter 'country': 'gb'
                parameter 'language': 'en'
            }
        }
    }

    response {
        status 200
        body(file("response/get_content_search_results_data_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}