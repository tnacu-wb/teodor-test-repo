package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Confirm reservation integration test")
    priority(1)
    request {
        method 'POST'
        urlPath('/v1/reservations/confirm')
        body('''
            {
                "paymentOption" : "PAY_ON_ARRIVAL",
                "reservationId" : "36116",
                "hotelId": "TKINPT",
                "operaPaymentMethod": "DAT",
                "paymentCard": {
                    "cardType": "AT",
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
        status 200
        body(file("confirm_Reservation_Success_200Response.json"))
        bodyMatchers {
            jsonPath('$.hotelId', byRegex('(?:^|\\W)TKINPT(?:$|\\W)'))
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }
}