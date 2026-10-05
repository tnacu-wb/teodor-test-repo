import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description("Get Customers Current Balance  belongs to given company-id, employee-id")
    request {
        method 'POST'
        url '/piba/account/transactions'
        body('''        
            {
                "schemeCustomerId":"782216",
                "tetheredUserGuid":"9a2cf458-9c10-461b-8dad-a8b5712653cc",
                "searchCriteria":{
                    "dateSearch":{
                        "dateFrom":"2020-01-01",
                        "dateTo":"2021-03-15",
                        "transactionTypes":"Both"
                    },
                    "invoiceNumberSearch":{
                        "invoiceNumber":5
                    }
                },
                "pagingRequest":{
                    "page":5,
                    "maximumDisplayRows":10
                }
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
            header('session-id', '13245312321345')
            header("Authorization", "Bearer dummy_value10")
        }
    }

    response {
        status 400
        body('''
        {
            "code": "001",
            "details": [
                "searchCriteria Invalid selection criteria"
            ]
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}