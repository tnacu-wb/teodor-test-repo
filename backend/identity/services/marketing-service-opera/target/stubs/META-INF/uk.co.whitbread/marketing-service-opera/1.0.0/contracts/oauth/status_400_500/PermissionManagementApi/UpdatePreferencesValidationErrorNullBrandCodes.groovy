package contracts.oauth.status_400_500.PermissionManagementApi

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Try to edit newsletter preferences and receive a bad request error")
    request {
        method 'PUT'
        url '/marketing/newsletter/phone/rachel.cody@housemark.co.uk'
        headers {
            header('Content-Type', 'application/json')
            header('Authentication', 'Bearer dummyToken')
        }
        body '''
        {
          "brandCodes": null,
          "optIn": false,
          "secondPartyOptIn": true,
          "thirdPartyVendorsOptIn": true,
          "doubleOptIn": false,
          "customer" : {
            "countryOfResidence" : "DE",
            "title" : "Mr",
            "firstName" : "liam",
            "lastName" : "wilson",
            "nationality" : "GB",
            "language": "en",
            "userId" : "liam.wilson.test1"
          }
        }
        '''
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