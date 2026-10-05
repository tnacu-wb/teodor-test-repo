package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Propose a new credit limit")
    request {
        method 'POST'
        url '/piba/account/proposecreditlimit'
        body('''        
            {
                  "proposedCreditLimit": 800,
                  "schemeCustomerId": 78359255,
                  "tetheredUserGuid": "97BBBCF7-C5CA-4F95-57GD-3BE3055E333F"
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
            header("Authorization", "Bearer dummy_value10")
        }
    }
    response {
        status 400
        body('''
            {
                "code": "2604",
                "details": [
                    "InvalidAction"
                ]
            }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
