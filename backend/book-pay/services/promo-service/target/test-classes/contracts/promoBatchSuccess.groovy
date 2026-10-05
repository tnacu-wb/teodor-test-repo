package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("promo-batch-controller")
    request {
        method 'POST'
        urlPath('/api/promo/v1/batches') {
        }
    }

    response {
        status 200
        body("""
        {
           "batchId": "${regex('[0-9a-fA-F\\\\-]{36}')}",
           "createdAt": "${regex('([0-9]{4})-(1[0-2]|0[1-9])-(3[01]|0[1-9]|[12][0-9])T(2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])(\\\\\\\\.\\\\\\\\d+)?(Z|[+-][01]\\\\d:[0-5]\\\\d)')}",
           "status": "PENDING"
        }
        """)
        headers {
            header('Content-Type', 'application/json')
        }
    }
}