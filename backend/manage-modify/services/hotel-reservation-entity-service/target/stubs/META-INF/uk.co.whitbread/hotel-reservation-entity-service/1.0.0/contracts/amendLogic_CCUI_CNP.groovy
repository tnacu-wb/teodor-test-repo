package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    ignored()
    description("Amend Logic flow - Confirm Amend CCUI CNP")
    request {
        method 'PUT'
        urlPath('/v1/reservations/amend/confirmAmendLogic')
        body('''
{
    "originalBookingRef": "GAN-674ab8b3-a59b-49af-97f0-f789d99bdb0a",
    "tempBookingRef": "GAN-53219109-9ee9-404e-8843-77be149f8e12",
    "token": "token",
    "bookingChannel": {
        "channel": "CCUI",
        "subchannel": "WEB",
        "language": "EN"
    },
    "paymentOptionSelected": "PAY_ON_ARRIVAL",
    "environment": "https://www.dit.premierinn.digital",
    "emailAddress": "abc@test.com",
    "ccuiExtraItems": {
            "businessItems" : {
                "purchaseOrderNumber" : "Abc1234556",
                "customReferenceNumber" : "Cust543",
                "businessAllowances" : [{
                        "allowance": "meal deal",
                        "budget": 30,
                        "isAuthorised": true 
                    }],
                "businessNotes": "Bla bla bla"
            },
            "accountCompanyItems": {
                "companyNumber": "33547",
                "charges": "one two",
                "companyId": "company33"
            },
            "nonguaranteedItems": {
                "typeOfCaller" : "typeOfCaller"
            },
            "sendMail": true,
            "cardPresent":false,
            "addressCompanyName": "street company dfg"
    },
    "paymentRequest" : {
            "payment" : {
                "type":"a",
                "subType":"b"
            }
    },
    "paymentOption": "RESERVE_WITHOUT_CARD",
    "subPaymentType":""
    }

            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("amendLogic_CCUI_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}