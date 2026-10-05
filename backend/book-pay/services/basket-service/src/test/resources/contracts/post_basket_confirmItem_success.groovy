package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Confirm Item")
    request {
        method 'POST'
        urlPath('/v1/baskets/LON-7b97e3f6-dce9-4572-b3e1-a5aa91ebc4b7/items/1234/acks')
        body('''
            {
                "status":1,
                "reqAction": "CANCEL",
                "description":"test description",
                "reported_at": "2022-06-30T07:08:24.698635"
            }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 204
    }
}