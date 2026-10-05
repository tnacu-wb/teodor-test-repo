package contracts.oauth.status_400_500.CustomerHub

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Try to edit newsletter preferences and receive an Internal Server Error")
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
                    "contactChannelValue": "email500@housemark.co.uk",
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
            status 500
            body('''
                {
                    "code":"3001",
                    "details": ["Unknown error."]  
                }
        ''')
            headers {
                header('Content-Type', 'application/json')
            }
    }
}