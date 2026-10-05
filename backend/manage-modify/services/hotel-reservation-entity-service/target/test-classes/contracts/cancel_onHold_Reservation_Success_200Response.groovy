package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
  description("Cancel on hold reservation integration test")
  request {
    method 'PUT'
    urlPath('/v1/reservations/cancellations/on-hold')
    body('''
            {
              "hotelId":"MANOLD",             
              "basketReference" : "TestOnHold"
            }
            ''')
    headers {
      header('Content-Type', 'application/json')
    }
  }

  response {
    status 200
    body(file("cancel_onHold_Reservation_Success_200Response.json"))
    headers {
      header('Content-Type', 'application/json')
    }
  }
}