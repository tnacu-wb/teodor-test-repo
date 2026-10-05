package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    ignored()
    description("Confirm reservation integration test")
    priority(100)
    request {
        method 'POST'
        urlPath('/v1/reservations/confirm')
        body('''
            {
                "paymentOption" : "PAY_ON_ARRIVAL",
                "hotelId": "LONEUS",
                "reservationId" : "34865",
                "operaPaymentMethod": "DAA",
                "paymentCard": {
                    "cardType": "AA",
                    "token": "4111111111111111",
                    "expirationDate": "2025-03-31",
                    "cardHolderName": "Charlie",
                    "cardNumberLast4Digits": "1234"
                }
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 500
        body(file("confirm_Reservation_WrongCardType_500Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}