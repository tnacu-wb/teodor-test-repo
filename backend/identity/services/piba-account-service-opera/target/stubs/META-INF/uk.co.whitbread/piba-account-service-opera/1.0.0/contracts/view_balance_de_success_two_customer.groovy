import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get Customers Current Balance  belongs to given company-id, employee-id")
    request {
        method 'GET'
        url '/piba/account/balance'
        headers {
            header('Content-Type', 'application/json')
            header('session-id', '6589326768736')
            header('company-id', '987')
            header('employee-id', '2')
            header("Authorization", "Bearer dummy_value9")
        }
    }

    response {
        status 200
        body('''
            {
  "currentBalances": [
    {
      "schemeCustomerId": 19000034,
      "outstanding": {
        "amount": 0.0,
        "currencyCode": "GBP",
        "currencySymbol": "£"
      },
      "newTransactions": {
        "amount": 0.0,
        "currencyCode": "GBP",
        "currencySymbol": "£"
      },
      "available": {
        "amount": 200.0,
        "currencyCode": "GBP",
        "currencySymbol": "£"
      },
      "creditLimit": {
        "amount": 200.0,
        "currencyCode": "GBP",
        "currencySymbol": "£"
      },
      "currentBalance": {
        "amount": 0.0,
        "currencyCode": "GBP",
        "currencySymbol": "£"
      },
      "interimPayments": {
        "amount": 0.0,
        "currencyCode": "GBP",
        "currencySymbol": "£"
      },
      "tetheredGuid": "66b273de-8f40-4118-8bc8-29335ac1cf86",
      "accountName": "Frankfurt am Main",
      "accountNumber": "3089500001000070",
      "registrationRoles": [
        "ACCOUNT_HOLDER"
      ]
    },
    {
      "schemeCustomerId": 19000034,
      "outstanding": {
        "amount": 123.0,
        "currencyCode": "GBP",
        "currencySymbol": "£"
      },
      "newTransactions": {
        "amount": 567.0,
        "currencyCode": "GBP",
        "currencySymbol": "£"
      },
      "available": {
        "amount": 600.0,
        "currencyCode": "GBP",
        "currencySymbol": "£"
      },
      "creditLimit": {
        "amount": 270.0,
        "currencyCode": "GBP",
        "currencySymbol": "£"
      },
      "currentBalance": {
        "amount": 56.0,
        "currencyCode": "GBP",
        "currencySymbol": "£"
      },
      "interimPayments": {
        "amount": 789.0,
        "currencyCode": "GBP",
        "currencySymbol": "£"
      },
      "tetheredGuid": "b484d2e2-38e0-4f70-9845-d029f90cdc88",
      "accountName": "Frankfurt am Main",
      "accountNumber": "3089500001000070",
      "registrationRoles": [
        "ACCOUNT_HOLDER", 
        "CARD_HOLDER"
      ]
    }
  ],
  "totalRecordCount": 2
}        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}