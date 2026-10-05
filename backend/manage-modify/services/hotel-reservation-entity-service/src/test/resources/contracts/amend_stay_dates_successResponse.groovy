package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Amend flow - Amend stay dates for an existing basket")
    request {
        ignored()
        method 'POST'
        urlPath('/v1/reservations/amendStayDates')
        body('''
            {
                "tempBookingRef": "AKU-40d698a2-b56a-481f-89a7-754148c5865e",
                "newStartDate": "2030-02-20",
                "newEndDate": "2030-02-24",
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
        body(file("amendStayDates_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}