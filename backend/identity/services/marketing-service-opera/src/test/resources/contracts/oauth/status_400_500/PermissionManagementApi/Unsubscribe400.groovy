package contracts.oauth.status_400_500.PermissionManagementApi

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Try to unsubscribe from newsletter preferences and receive a bad request error")
    request {
        method 'DELETE'
        url '/marketing/newsletter/channel/HNL39288_28a5d984-7782-4e93-99a5-b2e9f8a746f3/unsubscribe'
        headers {
            header('Content-Type', 'application/json')
        }
        body '''
        {
          "customerId": "122434",
          "contactType" : "email"
        }
        '''
    }
    response {
        status 400
        body('''
             {
                "code": "001",
                "details": [
                    "brandCodes must not be null"
                ]
            }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}