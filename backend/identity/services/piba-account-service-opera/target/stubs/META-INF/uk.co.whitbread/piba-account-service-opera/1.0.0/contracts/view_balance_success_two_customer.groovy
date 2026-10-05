import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get Customers Current Balance  belongs to given company-id, employee-id")
    request {
        method 'GET'
        url '/piba/account/balance'
        headers {
            header('Content-Type', 'application/json')
            header('company-id', '123')
            header('employee-id', '55')
            header("Authorization", "Bearer dummy_value7")
        }
    }

    response {
        status 200
        body('''
        {
            "currentBalances": [
                {
                    "schemeCustomerId": 782810,
                    "outstanding": {
                        "amount": 8653.44,
                        "currencyCode": "GBP"
                    },
                    "newTransactions": {
                        "amount": 507.53,
                        "currencyCode": "GBP"
                    },
                    "available": {
                        "amount": 97.98,
                        "currencyCode": "GBP"
                    },
                    "creditLimit": {
                        "amount": 3000.00,
                        "currencyCode": "GBP"
                    },
                    "currentBalance": {
                        "amount": 2902.02,
                        "currencyCode": "GBP"
                    },
                    "interimPayments": {
                        "amount": 0.00,
                        "currencyCode": "GBP"
                    },
                    "tetheredGuid": "CE3C22D3-D413-4FB2-8219-1160FEE0FC21",
                    "accountName": "100216",
                    "accountNumber": "3089503200100301",
                    "registrationRoles": [
                        "ACCOUNT_HOLDER"
                    ]
                },
                {
                    "schemeCustomerId": 782831,
                    "outstanding": {
                        "amount": 0.00,
                        "currencyCode": "GBP"
                    },
                    "newTransactions": {
                        "amount": 0.00,
                        "currencyCode": "GBP"
                    },
                    "available": {
                        "amount": 10000.00,
                        "currencyCode": "GBP"
                    },
                    "creditLimit": {
                        "amount": 10000.00,
                        "currencyCode": "GBP"
                    },
                    "currentBalance": {
                        "amount": 0.00,
                        "currencyCode": "GBP"
                    },
                    "interimPayments": {
                        "amount": 0.00,
                        "currencyCode": "GBP"
                    },
                    "tetheredGuid": "1EA0F9EF-A09B-479A-AD27-148BC6B19483",
                    "accountName": "07734435",
                    "accountNumber": "3000003200100301",
                    "registrationRoles": [
                        "ACCOUNT_HOLDER"
                    ]
                }
            ],
            "totalRecordCount": 2
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}