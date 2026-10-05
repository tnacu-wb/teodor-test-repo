package contracts.oauth.status_200.PermissionManagementApi

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Confirm double opt in")
    request {
        method 'POST'
        url '/marketing/newsletter/channel/CHNL39288_28a5d984-7782-4e93-99a5-b2e9f8a746f3/confirm'
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
        status 204
    }
}