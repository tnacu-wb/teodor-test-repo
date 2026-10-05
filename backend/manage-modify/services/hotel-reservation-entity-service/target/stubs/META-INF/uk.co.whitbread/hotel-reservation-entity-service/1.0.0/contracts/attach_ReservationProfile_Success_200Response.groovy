package contracts.reservation


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Attach profile to reservations integration test")
    request {
        method 'POST'
        urlPath('/v1/reservations/profiles')
        body('''
            {
                "hotelId":"HOTELTEST",
                "profileId":"1234",
                "reservationIds": [
                    "1234"
                ]
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