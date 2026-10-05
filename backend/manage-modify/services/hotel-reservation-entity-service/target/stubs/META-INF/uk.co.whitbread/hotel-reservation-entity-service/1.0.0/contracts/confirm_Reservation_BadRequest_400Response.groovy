package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Confirm reservation integration test")
    priority(100)
    request {
        method 'POST'
        urlPath('/v1/reservations/confirm')
        body('''
            {
             
                "reservationId" : ""
               
            }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 422
        body(file("confirm_Reservation_BadRequest_400Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}