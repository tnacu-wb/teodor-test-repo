package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Remove a room based on basket ref and reservation id")
    request {
        method 'POST'
        urlPath('/v1/reservations/rooms/delete') {
            queryParameters {
                parameter 'tempBookingRef': 'AWM5555555'
                parameter 'reservationId': '6657777'
                parameter 'channel': 'PI'
                parameter 'subchannel': 'WEB'
                parameter 'language': 'EN'
                parameter 'token': 'token'
            }
        }
    }

    response {
        status OK()
        headers {
            header('Content-Type', 'application/json')
        }
        body '''
        {
            "tempBookingRef": "AWM5555555"
        }
        '''
    }
}

