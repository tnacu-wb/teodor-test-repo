package contracts.oauth.status_401.PermissionManagementApi

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Try to unsubscribe with an invalid or expired Azure Authentication")
    request {
        method 'DELETE'
        url '/marketing/newsletter/channel/CHNL401_28a5d984-7782-4e93-99a5-b2e9f8a746f3/unsubscribe'
        headers {
            header('Content-Type', 'application/json')
        }
        body '''
        {
          "brandCodes": [ "PINN" ],
          "customerId": "122434",
          "contactType" : "email"
        }
        '''
    }
    response {
            status 401
            body('''
                {
                    "code":"3001",
                    "details": ["You do not have permission to view this directory or page."]  
                }
        ''')
            headers {
                header('Content-Type', 'application/json')
            }
    }
}