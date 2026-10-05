package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Create reservation guest integration test")
    request {
        method 'POST'
        urlPath('/v1/reservations/guests')
        body('''
            {
              "basketReference": "AKU-276afd23-b1fd-4cbc-96f1-d0a1f814d4e7",
              "hotelId" : "TestId",
              "reasonForStay": "LEI",
              "booker": {
                "title": "Mrs",
                "firstName": "John",
                "lastName": "Carry",
                "emailAddress": "john.carry@email.com",
                "acceptFutureMailing": true,
                "mobile": "+3905678754",
                "landline": "+3905678754",
                "address": {
                     "addressType": "BUSINESS",
                     "postalCode": "WC2N 5DU",
                     "addressLine1": "4 Brockley Avenue",
                     "addressLine2": "addressline2",
                     "addressLine3": "addressline3",
                     "addressLine4": "addressline4",
                     "countryCode": "UK",
                     "cityName": "London",
                     "companyName": "companyName"
                }
              },
              "stayingGuests": [
                {
                    "sameAsBooker":"false",
                    "stayingGuestDetails": {
                        "title":"Mrs",
                        "firstName":"Debbie",
                        "lastName":"Doe",
                        "address":{
                            "addressType":"HOME",
                            "postalCode":"EC1A 1BB",
                            "addressLine1":"4 Brockley Avenue",
                            "addressLine2":"London District 2 ",
                            "addressLine3":"Greater London - sub district 2 ",
                            "addressLine4":"Greater London - street 22",
                            "countryCode":"GB",
                            "cityName":"London"
                        },
                        "additionalDetails": {
                            "dob": "1996-07-15",
                            "nationality": "Briton",
                            "passportNumber": "ABCD5679"
                        }
                    }
                }
              ],
              "sendEmailConfirmation": "true",
              "sendEmailInvoice": "true"           
        }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 201
        body(file("create_ReservationGuest_Success_201Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}