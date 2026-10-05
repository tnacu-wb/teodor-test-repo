package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Initiate Eckoh Payment")
    request {
        method 'POST'
            urlPath('/v1/baskets/ccui/LON-7b97e3f6-dce9-4572-b3e1-a5aa91ebc4b7/eckoh')
        body(file('request/post_ccuiEckoh_initiateEckohPayment_success.json'))
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body('''
            {
                "paymentId": "33661084018D"   
            }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}