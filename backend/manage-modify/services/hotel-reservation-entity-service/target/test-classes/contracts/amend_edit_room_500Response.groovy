package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    ignored()
    description("Amend flow - Edit room")
    request {
        method 'PUT'
        urlPath('/v1/reservations/amend/editRoom')
        body('''
            {
                "tempBookingRef" : "AKU-276afd23-b1fd-4cbc-96f1-d0a1f814d4e7",
                "reservationId": "9966557",
                "roomOccupancy" : {
                    "adultsNumber": 1,
                    "childrenNumber": 0,
                    "cotRequired": false
                },
                "leadGuest" : {
                    "title": "mrs",
                    "firstName": "cat",
                    "lastName": "dog",
                    "emailAddress": "dd@bb.com"
                },
                "roomType": "DB",
                "bookingChannel" : {
                    "channel": "PI",
                    "subchannel": "WEB",
                    "language": "EN"
                }
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 500
        body('''
        {
            "errCode": 500,
            "debugMessage": "Couldn't edit your room",
            "globalErrTextTemplate": "internal.server.exception"
        }
        ''')
    }
}