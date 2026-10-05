package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Apply discount")
    request {
        method 'PUT'
        urlPath('/v1/baskets/ccui/discount')
        body('''
             {
                "basketReference": "LON-7b97e3f6-dce9-4572-b3e1-a5aa91ebc4b7",
                "discountAmount": 0.2
             }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
    }
}

