import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description("Get Customers Current Balance  belongs to given company-id, employee-id")
    request {
        method 'POST'
        url '/piba/account/transactions'
        body('''        
            {
                "schemeCustomerId":"782216",
                "tetheredUserGuid":"f6e317cf-bcf3-4859-9f7f-84068f15571a",
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
        status 200
        body('''
        {
            "response": {
                "transactions": [
                    {
                        "invoiceDate": "2020-08-02",                        
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
                    },
                    {
                        "invoiceDate": "2020-08-02",
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
                                "invoiceLineItem": 3
                            }
                        ]
                    },
                    {
                        "invoiceDate": "2020-08-02",
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
                                "invoiceLineItem": 8
                            }
                        ]
                    },
                    {
                        "invoiceDate": "2020-08-02",
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
                                "invoiceLineItem": 9
                            }
                        ]
                    },
                    {
                        "invoiceDate": "2020-08-02",                        
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
                                "invoiceLineItem": 1
                            }
                        ]
                    },
                    {
                        "invoiceDate": "2020-08-02",
                        "transactionDate": "2020-02-13T21:39:00",
                        "netAmount": {
                            "amount": 179.97,
                            "currencyCode": "EUR"
                        },
                        "taxAmount": {
                            "amount": 34.20,
                            "currencyCode": "EUR"
                        },
                        "grossAmount": {
                            "amount": 214.17,
                            "currencyCode": "EUR"
                        },
                        "location": "Frankfurt",
                        "pan": "30895001*******0018",
                        "cardName": "Craig Penton",
                        "lineItems": [
                            {
                                "description": "Accommodation",
                                "quantity": 1,
                                "netAmount": {
                                    "amount": 179.97,
                                    "currencyCode": "EUR"
                                },
                                "taxAmount": {
                                    "amount": 34.20,
                                    "currencyCode": "EUR"
                                },
                                "grossAmount": {
                                    "amount": 214.17,
                                    "currencyCode": "EUR"
                                },
                                "guestName": "Craig Penton",
                                "invoiceLineItem": 27
                            }
                        ]
                    },
                    {
                        "invoiceDate": "2020-08-02",
                        "transactionDate": "2020-02-13T21:39:00",
                        "netAmount": {
                            "amount": 179.97,
                            "currencyCode": "EUR"
                        },
                        "taxAmount": {
                            "amount": 34.20,
                            "currencyCode": "EUR"
                        },
                        "grossAmount": {
                            "amount": 214.17,
                            "currencyCode": "EUR"
                        },
                        "location": "Frankfurt",
                        "pan": "30895001*******0018",
                        "cardName": "Craig Penton",
                        "lineItems": [
                            {
                                "description": "Accommodation",
                                "quantity": 1,
                                "netAmount": {
                                    "amount": 179.97,
                                    "currencyCode": "EUR"
                                },
                                "taxAmount": {
                                    "amount": 34.20,
                                    "currencyCode": "EUR"
                                },
                                "grossAmount": {
                                    "amount": 214.17,
                                    "currencyCode": "EUR"
                                },
                                "guestName": "Craig Penton",
                                "invoiceLineItem": 23
                            }
                        ]
                    },
                    {
                        "invoiceDate": "2020-08-02",
                        "transactionDate": "2020-02-13T21:39:00",
                        "netAmount": {
                            "amount": 179.97,
                            "currencyCode": "EUR"
                        },
                        "taxAmount": {
                            "amount": 34.20,
                            "currencyCode": "EUR"
                        },
                        "grossAmount": {
                            "amount": 214.17,
                            "currencyCode": "EUR"
                        },
                        "location": "Frankfurt",
                        "pan": "30895001*******0018",
                        "cardName": "Craig Penton",
                        "lineItems": [
                            {
                                "description": "Accommodation",
                                "quantity": 1,
                                "netAmount": {
                                    "amount": 179.97,
                                    "currencyCode": "EUR"
                                },
                                "taxAmount": {
                                    "amount": 34.20,
                                    "currencyCode": "EUR"
                                },
                                "grossAmount": {
                                    "amount": 214.17,
                                    "currencyCode": "EUR"
                                },
                                "guestName": "Craig Penton",
                                "invoiceLineItem": 25
                            }
                        ]
                    },
                    {
                        "invoiceDate": "2020-08-02",
                        "transactionDate": "2020-02-13T21:39:00",
                        "netAmount": {
                            "amount": 179.97,
                            "currencyCode": "EUR"
                        },
                        "taxAmount": {
                            "amount": 34.20,
                            "currencyCode": "EUR"
                        },
                        "grossAmount": {
                            "amount": 214.17,
                            "currencyCode": "EUR"
                        },
                        "location": "Frankfurt",
                        "pan": "30895001*******0018",
                        "cardName": "Craig Penton",
                        "lineItems": [
                            {
                                "description": "Accommodation",
                                "quantity": 1,
                                "netAmount": {
                                    "amount": 179.97,
                                    "currencyCode": "EUR"
                                },
                                "taxAmount": {
                                    "amount": 34.20,
                                    "currencyCode": "EUR"
                                },
                                "grossAmount": {
                                    "amount": 214.17,
                                    "currencyCode": "EUR"
                                },
                                "guestName": "Craig Penton",
                                "invoiceLineItem": 20
                            }
                        ]
                    },
                    {
                        "invoiceDate": "2020-08-02",
                        "transactionDate": "2020-02-13T21:39:00",
                        "netAmount": {
                            "amount": 179.97,
                            "currencyCode": "EUR"
                        },
                        "taxAmount": {
                            "amount": 34.20,
                            "currencyCode": "EUR"
                        },
                        "grossAmount": {
                            "amount": 214.17,
                            "currencyCode": "EUR"
                        },
                        "location": "Frankfurt",
                        "pan": "30895001*******0018",
                        "cardName": "Craig Penton",
                        "lineItems": [
                            {
                                "description": "Accommodation",
                                "quantity": 1,
                                "netAmount": {
                                    "amount": 179.97,
                                    "currencyCode": "EUR"
                                },
                                "taxAmount": {
                                    "amount": 34.20,
                                    "currencyCode": "EUR"
                                },
                                "grossAmount": {
                                    "amount": 214.17,
                                    "currencyCode": "EUR"
                                },
                                "guestName": "Craig Penton",
                                "invoiceLineItem": 19
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