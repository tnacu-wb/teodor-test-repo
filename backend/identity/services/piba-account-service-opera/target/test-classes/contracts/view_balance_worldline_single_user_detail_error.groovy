import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get Customers Current Balance  belongs to given company-id, employee-id")
    request {
        method 'GET'
        url '/piba/account/balance'
        headers {
            header('Content-Type', 'application/json')
            header('company-id', '123')
            header('employee-id', '43')
            header("Authorization", "Bearer dummy_value8")
        }
    }

    response {
        status 200
        body('''
        {
        "currentBalances":[
            {
                 "schemeCustomerId":0,
                 "outstanding":null,
                 "newTransactions":null,
                 "available":null,
                 "creditLimit":null,
                 "currentBalance":null,
                 "interimPayments":null,
                 "tetheredGuid":null,
                 "accountName":null,
                 "accountNumber":null,
                 "registrationRoles":null,
                 "errorCode":"Error while retrieving tethered user details"
            }
        ],
        "totalRecordCount":1
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}