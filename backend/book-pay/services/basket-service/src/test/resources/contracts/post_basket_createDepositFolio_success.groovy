package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Create deposit folios for a basket")
    request {
        method 'POST'
        urlPath('/v1/baskets/deposit-folios')
        body('''
            {
                "prepaidDeposits": [
                    {
                        "reservationId": "123reservation",
                        "paymentNo": "1",
                        "charges": [
                            {
                                "transactionCode": "t-code",
                                "postingQuantity": "12",
                                "postingReference": "p-ref",
                                "chargeAmount": {
                                    "amount": 12.4,
                                    "currencyCode": "US"
                                }
                            },
                            {
                                "transactionCode": "t-code2",
                                "postingQuantity": "13",
                                "postingReference": "p-ref",
                                "chargeAmount": {
                                    "amount": 12.4,
                                    "currencyCode": "US2"
                                }
                            }
                        ]
                    },
                    {
                        "reservationId": "123reservation123",
                        "paymentNo": "1",
                        "charges": [
                            {
                                "transactionCode": "t-code123",
                                "postingQuantity": "12123",
                                "postingReference": "p-ref123",
                                "chargeAmount": {
                                    "amount": 12.4,
                                    "currencyCode": "US"
                                }
                            },
                            {
                                "transactionCode": "t-code2123",
                                "postingQuantity": "13123",
                                "postingReference": "p-ref123",
                                "chargeAmount": {
                                    "amount": 12.4,
                                    "currencyCode": "US123"
                                }
                            }
                        ]
                    }
            
                ]
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 201
    }
}