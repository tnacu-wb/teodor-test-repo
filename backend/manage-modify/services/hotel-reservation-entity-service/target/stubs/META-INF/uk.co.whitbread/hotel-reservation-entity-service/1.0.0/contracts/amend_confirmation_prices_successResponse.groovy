package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    ignored()
    description("Amend Confirmation Prices")
    request {
        method 'GET'
        urlPath('/v1/reservations/amend/amendConfirmationPrices')
        body('''
            {
                "tempBookingRef" : "AKU-276afd23-b1fd-4cbc-96f1-d0a1f814d4e7",
                "originalBookingRef" : "AKU-276afd23-b1fd-4cbc-96f1-d0a1f8100000"
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("amendConfirmationPrices_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}