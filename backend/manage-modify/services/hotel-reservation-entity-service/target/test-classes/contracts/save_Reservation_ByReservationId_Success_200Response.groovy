package contracts
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Update reservation packages integration test")
    request {
        method 'PUT'
        urlPath ('/v1/reservations/ancillaries/reservation-id')
        body('''
        {
            "basketReferenceId": "TestId1234567",
            "arrivalDate": "2023-10-10",
            "departureDate": "2023-10-12",
            "hotelId":"HOTELCODE",
            "previousRoomsSelections": [
                {
                    "reservationId": "3514830",
                    "packagesSelection": [
                ]
                }
            ],
            "roomsSelections": [ 
                {
                    "reservationId": "3514830",
                    "packagesSelection": [
                        {
                            "id": "BFADBF", 
                            "noOfSelections": 1
                        },
                        {
                            "id": "CITYTAX", 
                            "noOfSelections": 1
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
        status 200
        body(file("save_Reservation_ByReservationId_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}