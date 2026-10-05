import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description("Get Customers invoices")
    request {
        method 'POST'
        url '/piba/account/invoices'
        body('''        
            {
                "schemeCustomerId":"9876543",
                "tetheredUserGuid":"trwtes-7cf-bcf3-asda-9f7f-84068f15571a",
                "searchCriteria":{
                    "dateFrom":"2020-05-05",
                    "dateTo":"2021-03-15"
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
            "code": "SchemaError",
            "details": [
            "SchemaError"
            ]
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}