import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get Customers Current Balance  belongs to given company-id, employee-id")
    request {
        method 'GET'
        url '/piba/account/balance'
        headers {
            header('Content-Type', 'application/json')
            header('company-id', '123')
            header('employee-id', '40')
            header("Authorization", "Bearer dummy_value2")
        }
    }

    response {
        status 200
        body('''
        {
            "currentBalances": [
                {
                    "schemeCustomerId": 783582,
                    "outstanding": null,
                    "newTransactions": null,
                    "available": null,
                    "creditLimit": null,
                    "currentBalance": null,
                    "interimPayments": null,
                    "tetheredGuid": "9B1B3ED5-F61C-403E-8A56-59F0B625F856",
                    "accountName": "07788435",
                    "accountNumber": "3089503200100301",
                    "registrationRoles": [
                        "CARD_HOLDER"
                    ],
                    "errorCode": "Error while retrieving balance"
                }
            ],
            "totalRecordCount": 1
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}