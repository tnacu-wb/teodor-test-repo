package contracts
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Create reservation integration test")
    request {
        method 'PUT'
        urlPath ('/v1/reservations/ancillaries')
        body('''
        {
            "basketReferenceId": "TestId1234567",
            "arrivalDate": "2022-04-02",
            "departureDate": "2022-04-03",
            "roomsSelections": [ {
              "packagesSelection": [
                    {
                        "id": "PIBTEST",
                        "noOfSelections": 1
                    }
              ]
            }],
            "hotelId":"HOTELCODE"
        }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("save_Reservation_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}