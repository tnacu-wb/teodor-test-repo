package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Update Business Items for a reservation integration test")
    request {
        method 'PUT'
        urlPath('/v1/reservations/business')
        body('''
              {
                "hotelId": "HOTELCODE",
                "reservationIds": ["123456"],
                "businessItems": {
                    "businessNotes":"TestingNote is authorized",
                    "purchaseOrderNumber":"1234521345",
                    "customReferenceNumber":"ABC123456",
                     "businessAllowances":[
                            {
                                "allowance": "ultimateWifi",
                                "budget":"0.0",
                                "isAuthorised":true
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
        status 200
    }
}