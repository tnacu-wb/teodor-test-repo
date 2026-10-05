import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description("Get Customers Current Balance  belongs to given company-id, employee-id")
    request {
        method 'POST'
        url '/piba/account/transactions'
        body('''        
            {
                "schemeCustomerId":"782215",
                "tetheredUserGuid":"f6e317cf-bcf3-4859-9f7f-84068f15571b",
                "searchCriteria":{
                "dateSearch":{
                    "dateFrom":"2020-01-01",
                    "dateTo":"2021-03-15",
                    "transactionTypes":"Both"
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
        status 500
        body('''
        {
            "code": "ValidationError",
            "details": [
                "UserNotFoundForSystem"
            ]
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}