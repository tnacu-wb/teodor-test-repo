package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("rules-agent-controller")
    priority(1)
    request {
        method 'GET'
        urlPath('/v1/rules/ccui/rbac') {
            queryParameters {
                parameter 'resourceId': 'CCUI_RES1'
            }
        }
    }

    response {
        status 422
        headers {
            header('Content-Type', 'application/json')
        }
    }
}