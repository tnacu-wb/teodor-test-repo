package contracts
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Initiate CCUI payment with PAY_ON_ARRIVAL option")
    request {
        method 'POST'
        urlPath('/v1/baskets/ccui/LON-7b97e3f6-dce9-4572-b3e1-a5aa91ebc4b7/pay')
        body('''
            {
                "paymentOption": "NEW_PIBA",
                "subPaymentType":"PIBAGB",
                "ccuiExtraItems": {
                    "businessItems": {
                    "purchaseOrderNumber": "1234521345",
                    "customReferenceNumber": "ABC123456",
                    "businessAllowances": [
                        {
                            "allowance": "carParking",
                            "budget": "0.0",
                            "isAuthorised":true
                        },
                        {
                            "allowance": "dinner",
                            "budget": "30.0",
                            "isAuthorised":true
                        },
                        {
                            "allowance": "alcohol",
                            "budget": "0.0",
                            "isAuthorised":true
                        }
                    ]
                },
                    "cardPresent" : true,
                    "addressCompanyName":"soft",
                    "accountCompanyItems": {
                        "companyNumber": "1234567",
                        "charges": "park;breakfeast"
                    },
                    "nonguaranteedItems": {
                        "typeOfCaller": "text"
                    }
                },
                "paymentRequest": {
                    "requestId": "",
                    "payment": {
                        "type": "PIBA",
                        "subType": "MOTO",
                        "billing": {
                            "firstName": "test",
                            "lastName": "test2",
                            "address": {
                                "addressLine1": "Mount Pleasant",
                                "addressLine2": "blah2",
                                "addressLine3": "blah3",
                                "postalCode": "SG89ES",
                                "country": "GB",
                                "state": "Hertfordshire",
                                "companyName": "{Whitbread}|"
                            }
                        }, 
                        "card" : {
                            "cardHolderAddress": {
                                    "line1": "blah",
                                    "line1": "blah2",
                                    "line1": "blah3",
                                    "postalCode": "SG89ES",
                                    "country": "GB",
                                    "state": "Hertfordshire",
                                    "addressType": "HOME"
                                }
                           }
                    },
                    "booking": {
                        "language": "en",
                        "type": "PAY_ON_ARRIVAL",
                        "journey": "BOOKING",
                        "channel": "CCC",
                        "businessSite": {
                            "identifier": "LONHOL",
                            "type": "HOTEL",
                            "location": "LONHOL",
                            "name": "London Hotel"
                        }
                    }
                }
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 400
        body(file("response/post_ccuiPayment_initiatePayment_payOnArrival_CompanyNameValidationException.json"))
        bodyMatchers {
            jsonPath('$.debugMessage',
                    byRegex("^Invalid company name \\{Whitbread\\}\\|\\. Valid characters are letters.*"))
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
