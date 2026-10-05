package contracts.oauth.status_400_500.CustomerHub

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Try to get newsletter preferences and receive an Internal Server Error")
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
                    "contactChannelType": "Email",
                    "contactChannelValue": "email500@housemark.co.uk"
                },
            "brandCodes": [
                "PINN", 
                "WINN"
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