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
                "tempBookingRef" : "AKU-40d698a2-b56a-481f-89a7-754148c5865e",
                "reservationId": "12345",
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
        body(file("amendEditRoom_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}