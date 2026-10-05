package contracts.oauth.status_400_500.CustomerHub

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Try to get newsletter preferences and receive a bad request error")
    request {
        method 'POST'
        url '/marketing/hotels/newsletter/get'
        headers {
            header('Content-Type', 'application/json')
        }
        body '''
        {
            "requestId": "fsytdfxusdgcilugasld",
            "contactChannel": 
                {
                    "contactChannelType": "Telephone",
                    "contactChannelValue": "+447777777777"
                },
            "brandCodes": [
                "PINN", 
                "WINN"
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