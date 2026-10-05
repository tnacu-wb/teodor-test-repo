package contracts.oauth.status_200.CustomerHub

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Edit newsletter preferences")
    request {
        method 'PUT'
        url '/marketing/hotels/newsletter/edit'
        headers {
            header('Content-Type', 'application/json')
        }
        body '''
        {
            "userId": "name.surname",
            "subscriptionData": [
                {
                    "contactChannelType": "Email",
                    "contactChannelValue": "email_success@gmail.com",
                    "contactChannelPermission": "false",
                    "brandCodes": [
                        "PINN", 
                        "WINN"
                    ],
                    "contentPermission": {
                        "secondParty": true,
                        "thirdParty": true
                    }
                }
            ]
        }
        '''
    }
    response {
        status 204
    }
}