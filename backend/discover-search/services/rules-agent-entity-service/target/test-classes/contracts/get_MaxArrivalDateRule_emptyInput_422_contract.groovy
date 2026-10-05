package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("rules-agent-controller")
    priority(1)
    request {
        method 'GET'
        urlPath('/v1/rules/max-arrival-date') {
            queryParameters {
                parameter 'channelId': ''

            }
        }
    }

    response {
        status 422
        body(file("response/get_MaxArrivalDateRule_emptyInput_422_contract.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}