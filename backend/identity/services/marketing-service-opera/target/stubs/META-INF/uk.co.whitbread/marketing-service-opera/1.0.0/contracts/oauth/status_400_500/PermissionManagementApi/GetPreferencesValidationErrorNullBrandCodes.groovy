package contracts.oauth.status_400_500.PermissionManagementApi

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Try to get newsletter preferences and receive a bad request error")
    request {
        method 'GET'
        url '/marketing/newsletter/phone/rachel.cody@housemark.co.uk'
        headers {
            header('Content-Type', 'application/json')
            header('Authentication', 'Bearer dummyToken')
        }
    }
        response {
            status 400
            body('''
                {
                    "code":"001",
                    "details": ["brandCodes must not be null"]  
                }
        ''')
            headers {
                header('Content-Type', 'application/json')
            }
    }
}