package contracts.updateCustomer

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description('Send a rest password with an Auth0 link.')
    request {
        method 'POST'
        url'/v2/auth/hotels/forgot-password'
        body(
                '''
            {
                "username": "customer@test.com",
                "url": "http://test.premierinn.com"
            }
            '''
        )
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        headers {
            header('Content-Type', 'application/json')
        }
    }

}
