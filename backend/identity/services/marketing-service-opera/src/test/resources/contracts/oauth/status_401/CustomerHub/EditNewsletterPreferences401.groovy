package contracts.oauth.status_401.CustomerHub

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
                    "contactChannelValue": "unauthorized@gmail.com",
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