package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    ignored()
    description("Amend summary")
    request {
        method 'GET'
        urlPath('/v1/reservations/amend/summary')
        body('''
            {
                "tempBookingRef" : "AKU-276afd23-b1fd-4cbc-96f1-d0a1f814d4e7",
                "originalBookingRef" : "AKU-276afd23-b1fd-4cbc-96f1-d0a1f8100000",
                "token" : "4111111111111111",
                "channel": "PI",
                "subchannel" : "WEB",
                "language": "EN"
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("amendSummary_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}