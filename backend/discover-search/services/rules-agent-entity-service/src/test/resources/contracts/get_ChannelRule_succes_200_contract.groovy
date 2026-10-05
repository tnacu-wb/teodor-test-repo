package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("rules-agent-controller")
    priority(1)
    request {
        method 'GET'
        urlPath('/v1/rules/channel-info') {

            queryParameters {
                parameter 'channel': "PI"
                parameter 'subchannel': "WEB"
                parameter 'language' : "EN"
                parameter 'pms': "OP"

            }
        }
    }

    response {
        status 200
        body(file("response/get_ChannelRule_success_200_contract.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}