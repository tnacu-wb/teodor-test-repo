import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description("Get Customers Current Balance  belongs to given company-id, employee-id")
    request {
        method 'POST'
        url '/piba/account/transactions'
        body('''        
            {
                "schemeCustomerId":"782217",
                "tetheredUserGuid":"f6e317cf-bcf3-4859-9f7f-84068f15571f",
                "searchCriteria":{
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
            header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImI5OGZkOWY5LTBmOTQtNDI3Yy1hMjE4LWIzMzg0ZDRkZjNmMiJ9")
        }
    }

    response {
        status 200
        body('''
        {
            "response": {
                "transactions": [
                    {
                        "invoiceDate": "2020-08-02",       
                        "invoiceNo":5,                 
                        "transactionDate": "2020-02-13T18:40:00",
                        "netAmount": {
                            "amount": 719.89,
                            "currencyCode": "GBP"
                        },
                        "taxAmount": {
                            "amount": 35.99,
                            "currencyCode": "GBP"
                        },
                        "grossAmount": {
                            "amount": 755.88,
                            "currencyCode": "GBP"
                        },
                        "location": "Jersey St Helier (Charing Cros",
                        "pan": "30895001*******0018",
                        "cardName": "Craig Penton",
                        "lineItems": [
                            {
                                "description": "Accommodation",
                                "quantity": 1,
                                "netAmount": {
                                    "amount": 719.89,
                                    "currencyCode": "GBP"
                                },
                                "taxAmount": {
                                    "amount": 35.99,
                                    "currencyCode": "GBP"
                                },
                                "grossAmount": {
                                    "amount": 755.88,
                                    "currencyCode": "GBP"
                                },
                                "guestName": "Craig Penton",
                                "invoiceLineItem": 5
                            }
                        ]
                    }
                    
                ]
            },
            "pagingResult": {
                "fromRecord": 41,
                "toRecord": 50,
                "totalRecordCount": 594,
                "lastPage": 60
            }
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}