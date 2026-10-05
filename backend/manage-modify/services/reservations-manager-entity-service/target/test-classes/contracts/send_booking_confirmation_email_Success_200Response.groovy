package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("reservations-manager-entity-controller")
    request {
        method 'POST'
        headers {
            header('Content-Type', 'application/json')
            header('WB-Authorization', 'Bearer dummy-token')
        }
        urlPath('/v1/bookings/confirmation') {
            body ('''
                   {
                       "email": "test@mail.com",
                       "hotelId": "LONMON"
                   }
                '''
            )
        }
    }
    response {
        status 200
    }
}