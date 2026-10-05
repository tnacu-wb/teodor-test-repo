package contracts.oauth.status_400_500.CustomerHub

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Try to edit newsletter preferences and receive a bad request error")
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
                    "contactChannelType": "Telephone",
                    "contactChannelValue": "+447777777777",
                    "contactChannelSubType": "Mobile",
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
            status 400
            body('''
                {
                    "code":"3001",
                    "details": ["Required properties not found or invalid. Please ensure request contains all required properties. Ensure brand codes supplied are valid."]  
                }
        ''')
            headers {
                header('Content-Type', 'application/json')
            }
    }
}