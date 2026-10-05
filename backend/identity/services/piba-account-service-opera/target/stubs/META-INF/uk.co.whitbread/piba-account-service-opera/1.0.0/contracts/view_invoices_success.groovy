import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get Customers invoices")
    request {
        method 'POST'
        url '/piba/account/invoices'
        body('''        
            {
                "schemeCustomerId":"123456",
                "tetheredUserGuid":"f6e317cf-bcf3-asda-9f7f-84068f15571a",
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
        status 200
        body('''
            {
              "response": {
                "invoices": [
                  {
                    "broughtForward": {
                      "amount": 755.88,
                      "currencyCode": "GBP",
                      "currencySymbol": "£"
                    },
                    "fileAutoID": 123456,
                    "invoiceNo": 123456780,
                    "invoiceValue": {
                      "amount": 55.80,
                      "currencyCode": "GBP",
                      "currencySymbol": "£"
                    },
                    "overdueBalance": {
                      "amount": 755.88,
                      "currencyCode": "GBP",
                      "currencySymbol": "£"
                    },
                    "paymentsReceived": {
                      "amount": 35.99,
                      "currencyCode": "GBP",
                      "currencySymbol": "£"
                    },
                    "statementBalance": {
                      "amount": 155.88,
                      "currencyCode": "GBP",
                      "currencySymbol": "£"
                    },
                    "statementDate": "2020-08-02"
                  }
                ]
              },
              "pagingResult": {
                "fromRecord": 1,
                "toRecord": 10,
                "totalRecordCount": 1,
                "lastPage": 1
              }
            }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}