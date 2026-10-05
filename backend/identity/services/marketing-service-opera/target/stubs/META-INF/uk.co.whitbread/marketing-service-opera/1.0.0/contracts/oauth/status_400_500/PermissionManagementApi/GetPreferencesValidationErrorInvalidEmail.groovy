package contracts.oauth.status_400_500.PermissionManagementApi

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Try to get newsletter preferences and receive a bad request error")
    request {
        method 'GET'
        url '/marketing/newsletter/email/rachel.cody@housemark.c?brandCodes=PINN,WINN'
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
                    "details": ["Invalid email"]  
                }
        ''')
            headers {
                header('Content-Type', 'application/json')
            }
    }
}