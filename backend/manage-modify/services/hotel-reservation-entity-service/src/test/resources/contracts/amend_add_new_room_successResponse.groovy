package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    ignored()
    description("Amend flow - Add new room to an existing basket")
    request {
        method 'POST'
        urlPath('/v1/reservations/amend/addNewRoom')
        body('''
            {
                "tempBookingRef" : "AKU-276afd23-b1fd-4cbc-96f1-d0a1f814d4e7",
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
        status 200
        body(file("amendAddNewRoom_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}