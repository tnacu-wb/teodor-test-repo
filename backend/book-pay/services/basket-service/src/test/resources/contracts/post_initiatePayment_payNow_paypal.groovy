package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Initiate PayPal payment with PAY_NOW option")
    request {
        method 'POST'
        urlPath('/v1/baskets/LON-7b97e3f6-dce9-4572-b3e1-a5aa91ebc4b7/pp-pay')
        body('''
            {
                "requestId": "cfb312f7-c1cd-42cb-8670-b39eb166dfc6",
                "payment": {
                    "type": "PAYPAL",
                    "subType": "MIT",
                    "environment": "https://premier-inn.poc.opera.whitbread.digital",
                    "paypalNonce": "2300eefb-d7fb-11d4-73ba-88b3e44aedce",
                    "paypalDeviceData": "testDeviceData",
                    "billing": {
                        "firstName": "John",
                        "lastName": "Smith",
                        "title": "Mr",
                        "email": "john@smith.com",
                        "telephone": "40764702111",
                        "sameAsBookerAddress": false,
                        "address": {
                            "addressLine1": "Mount Pleasant Mail Centre",
                            "addressLine2": "Farringdon Road",
                            "addressLine3": "",
                            "addressLine4": "",
                            "postalCode": "EC1A 1BB",
                            "state": "LONDON",
                            "country": "GB",
                            "companyName": "",
                            "addressType": "HOME"
                        }
                    }
                },
                "booking": {
                    "type": "PAY_NOW",
                    "language": "en",
                    "journey": "BOOKING",
                    "channel": "PI",
                    "arrivalDate": "2025-09-01",
                    "departureDate": "2025-09-03",
                    "businessSite": {
                        "identifier": "WORHIG",
                        "type": "HOTEL",
                        "location": "WORHIG",
                        "name": "London Hotel"
                    },
                    "leadGuest": {
                        "name": "Mr John Smith",
                        "registered": true,
                        "registeredSince": "2021-02-05",
                        "previousBookings": 0
                    },
                    "rooms": [
                        {
                            "rate": "FLEXRATE",
                            "type": "DOUBLE",
                            "adultsNumber": 1
                        }
                    ]
                }
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 500
        body(file("response/post_initiatePayment_payNow_Paypal_error.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}