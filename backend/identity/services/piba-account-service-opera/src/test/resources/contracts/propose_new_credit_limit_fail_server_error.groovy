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
                  "schemeCustomerId": 783592344,
                  "tetheredUserGuid": "97BBBCF7-C5CA-4F95-57ED-3BE3055E333F"
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
            header("Authorization", "Bearer dummy_value10")
        }
    }
    response {
        status 500
        body('''
            {"code":"AuthenticationError","details":["AuthenticationError"]}
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
