package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Tether By Account Request Success - Save in CDH")
    request {
        method 'POST'
        url '/business/tether'
        body (
                '''
                {
                  "linkCode": "qyg-ujn-xes",
                  "linkId": 3089503200100176,
                  "memorableWord": "zoo",
                  "saveInCdh": true
                }
            '''
        )
        headers {
            header('Content-Type', 'application/json;charset=UTF-8')
            header('Authorization', 'Bearer test')
        }
    }

    response {
        status 201
        body (
            '''
            {
                "guid": "1206577c-30f6-463f-9eff-6bfd046b5cee"
            }
            '''
        )
    }
}