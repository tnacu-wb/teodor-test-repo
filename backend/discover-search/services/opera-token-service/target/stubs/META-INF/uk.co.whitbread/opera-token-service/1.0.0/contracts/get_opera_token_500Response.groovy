package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get access token for provider - error case when token service fails")
    request {
        method 'GET'
        urlPath('/v1/tokens/invalid-provider/access-token') {
        }
    }

    response {
        status 500
        body('''
        {
            "errCode": "974",
            "debugMessage": "Unable to acquire access token for invalid-provider",
            "globalErrTextTemplate": "internal.server.exception"
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
