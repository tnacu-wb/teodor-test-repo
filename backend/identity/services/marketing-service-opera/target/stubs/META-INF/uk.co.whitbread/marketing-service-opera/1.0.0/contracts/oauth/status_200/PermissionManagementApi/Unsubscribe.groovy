package contracts.oauth.status_200.PermissionManagementApi

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Unsubscribe from newsletter preferences")
    request {
        method 'DELETE'
        url '/marketing/newsletter/channel/CHNL40088_28a5d984-7782-4e93-99a5-b2e9f8a746f3/unsubscribe'
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