import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get Customers Current Balance  belongs to given company-id, employee-id")
    request {
        method 'GET'
        url '/piba/account/balance'
        headers {
            header('Content-Type', 'application/json')
            header('company-id', '123')
            header('employee-id', '15')
            header("Authorization", "Bearer dummy_value3")
        }
    }

    response {
        status 200
        body('''
            {
   "currentBalances":[
      {
         "schemeCustomerId":782810,
         "outstanding":{
            "amount":8653.44,
            "currencyCode":"GBP",
            "currencySymbol":"£"
         },
         "newTransactions":{
            "amount":507.53,
            "currencyCode":"GBP",
            "currencySymbol":"£"
         },
         "available":{
            "amount":97.98,
            "currencyCode":"GBP",
            "currencySymbol":"£"
         },
         "creditLimit":{
            "amount":3000.0,
            "currencyCode":"GBP",
            "currencySymbol":"£"
         },
         "currentBalance":{
            "amount":2902.02,
            "currencyCode":"GBP",
            "currencySymbol":"£"
         },
         "interimPayments":{
            "amount":0.0,
            "currencyCode":"GBP",
            "currencySymbol":"£"
         },
         "tetheredGuid":"CE3C22D3-D413-4FB2-8219-1160FEE0FC21",
         "accountName":"100216",
         "accountNumber":"3089503200100301",
         "registrationRoles":[
            "ACCOUNT_HOLDER"
         ]
      },
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
   "totalRecordCount":2
}
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}