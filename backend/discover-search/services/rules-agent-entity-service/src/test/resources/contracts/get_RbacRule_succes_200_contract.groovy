package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("rules-agent-controller")
    priority(1)
    request {
        method 'GET'
        urlPath('/v1/rules/ccui/rbac') {
            queryParameters {
                parameter 'resourceId': "CC_Role01"
                parameter 'roleIdList': "AGENT_ROLE"
            }
        }
    }

    response {
        status 200
        body('''
        {
          "hasAccess": true
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}