package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Initiate Eckoh Payment")
    request {
        method 'GET'
            urlPath('/v1/baskets/ccui/LON-7b97e3f6-dce9-4572-b3e1-a5aa91ebc4b7/eckoh')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body('''
            {
                "status": "PENDING"   
            }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}