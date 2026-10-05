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
        urlPath('/v1/bookings/invoice') {
            body ('''
                   {
                       "email": "test@mail.com",
                       "hotelId": "LONMON",
                       "invoiceRecordNumber": "15",
                       "bookingReference": "bookingReference"
                   }
                '''
            )
        }
    }
    response {
        status 200
    }
}