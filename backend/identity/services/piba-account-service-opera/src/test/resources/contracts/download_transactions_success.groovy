package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get Download Transactions List")
    request {
        method 'GET'
        url '/piba/account/transactions/download/782216/f6e317cf-bcf3-4859-9f7f-84068f15571a'
        headers {
            header('Content-Type', 'application/json')
            header('session-id', '13245312321345')
            header("Authorization", "Bearer dummy_value10")
        }
    }

    response {
        status 200
        headers {
            header('Content-Disposition', 'attachment; filename=Transactions.csv')
        }
    }
}
