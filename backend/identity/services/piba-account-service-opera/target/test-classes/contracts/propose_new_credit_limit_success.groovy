package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Propose a new credit limit")
    request {
        method 'POST'
        url '/piba/account/proposecreditlimit'
        body('''        
            {
                  "proposedCreditLimit": 670,
                  "schemeCustomerId": 78359222,
                  "tetheredUserGuid": "97BBBCF7-C5CA-4F95-56ED-3BE3055E333F"
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
            header("Authorization", "Bearer dummy_value10")
        }
    }
    response {
        status 200
        body('''
            {
                "requestId": "892198b7-818f-4ee5-8b55-02b0f52e27fb"
            }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
