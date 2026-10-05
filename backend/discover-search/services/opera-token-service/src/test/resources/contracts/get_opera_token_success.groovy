package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get access token for provider - success case")
    request {
        method 'GET'
        urlPath('/v1/tokens/ohip/access-token') {
        }
    }

    response {
        status 200
        body('''
        {
            "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.uXrIhrveuvbR4tD1ULholQObboLVC-wIfJOEVElEzcs",
            "tokenType": "Bearer",
            "expiresIn": 3600,
            "issuedAt": "2025-08-06T11:32:00Z"
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
