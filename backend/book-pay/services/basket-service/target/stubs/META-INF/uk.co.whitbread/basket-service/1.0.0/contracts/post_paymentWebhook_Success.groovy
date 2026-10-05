package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Payment webhook test success")
    request {
        method 'POST'
        urlPath('/v1/baskets/LON-7b97e3f6-dce9-4572-b3e1-a5aa91ebc4b7/payment-webhook')
        body('''{
            "reference": "LON-7b97e3f6-dce9-4572-b3e1-a5aa91ebc4b7",
            "paymentId": "67827113162D",
            "paymentStatus": "SUCCESS",
            "bookingReference": "LONHOL5778172",
            "countryCode" : "GB",
            "language": "en",
            "firstName": "Lari",
            "lastName": "Mil",
            "channel": "WEB",
            "last4Digits": "1234",
            "cardSchemeId": "VI",
            "token": "4216333880397891103",
            "expiry": "04/24",
            "fraudCheckDecision": ""
        }''')
        headers {
            header('X-WHIT-API-KEY', 'b246acfc-2271-4c0d-ac39-02554040b499')
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 202
        body('''
            {
                "reference": "LON-7b97e3f6-dce9-4572-b3e1-a5aa91ebc4b7"
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}