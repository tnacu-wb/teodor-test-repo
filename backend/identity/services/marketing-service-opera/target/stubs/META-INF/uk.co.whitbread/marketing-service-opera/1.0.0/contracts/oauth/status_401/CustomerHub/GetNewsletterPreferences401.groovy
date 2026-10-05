package contracts.oauth.status_401.CustomerHub

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Try to get newsletter preferences with an invalid or expired Azure Authentication")
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
                    "contactChannelValue": "unauthorized@gmail.com"
                },
            "brandCodes": [
                "PINN", 
                "WINN"
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