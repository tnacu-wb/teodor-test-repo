import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get Customers Current Balance  belongs to given company-id, employee-id")
    request {
        method 'GET'
        url '/piba/account/balance'
        headers {
            header('Content-Type', 'application/json')
            header('session-id', '13245312321345')
            header('company-id', '123')
            header('employee-id', '10')
            header("Authorization", "Bearer dummy_value4")
        }
    }

    response {
        status 400
        body('''
        {
            "code": "2604",
            "details": [
                "Error retrieving tetheredGuids"
            ]
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}