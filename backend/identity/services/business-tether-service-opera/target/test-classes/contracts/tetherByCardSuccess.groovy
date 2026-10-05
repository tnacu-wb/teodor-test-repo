package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Tether By Card Request Success")
    request {
        method 'POST'
        url '/business/tether'
        body (
                '''
                {
                  "linkCode": "mtr-36s-unk",
                  "linkId": 3089500110017600026,
                  "memorableWord": "word"
                }
            '''
        )
        headers {
            header('Content-Type', 'application/json;charset=UTF-8')
            header("Authorization", "dummy_value")
        }
    }

    response {
        status 201
        body (
            '''
            {
                "guid": "1206577c-30f6-463f-9eff-6bfd046b5cde"
            }
            '''
        )
    }
}