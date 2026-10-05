package contracts.oauth.status_200.PermissionManagementApi

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Edit newsletter preferences")
    request {
        method 'PUT'
        url '/marketing/newsletter/email/email_success@gmail.com'
        headers {
            header('Content-Type', 'application/json')
            header('Authentication', 'Bearer dummyToken')
        }
        body '''
        {
          "brandCodes": [ "PINN" ],
          "optIn": false,
          "secondPartyOptIn": true,
          "thirdPartyVendorsOptIn": true,
          "doubleOptIn": false,
          "customer" : {
            "countryOfResidence": "DE",
            "title" : "Mr",
            "firstName" : "liam",
            "lastName" : "wilson",
            "nationality" : "GB",
             "language": "de",
            "userId" : "liam.wilson.test1"
          }
        }
        '''
    }
    response {
        status 204
    }
}