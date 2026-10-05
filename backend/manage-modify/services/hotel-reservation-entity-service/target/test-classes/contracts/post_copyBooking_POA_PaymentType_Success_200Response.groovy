package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Copy reservations integration test")
    request {
        method 'POST'
        urlPath('/v1/reservations/copy')
        body('''
            {
                "originalBasketReference": "AEG5222658",
                "bookingChannel": {
                    "channel": "PI",
                    "subchannel": "WEB",
                    "language": "EN"
                },
                "token": "token"
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("post_copyBooking_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}