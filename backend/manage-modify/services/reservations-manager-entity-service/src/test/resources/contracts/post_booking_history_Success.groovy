package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("reservations-manager-entity-controller")
    request {
        method 'POST'
        headers {
            header('Content-Type', 'application/json')
            header('WB-Authorization', 'Bearer test==')
        }
        urlPath('/v1/bookings/history') {
            body ('''
                   {
                       "business": false,
                       "companyId": "companyId",
                       "employeeId": "employeeId",
                       "filterValue": "filterValue",
                       "includeCheckInBookings": false,
                       "continuationToken": "continuationToken",
                       "channel": "pi",
                       "subchannel": "WEB"
                   }
                '''
            )
        }
    }
    response {
        status 200
        body(file("response/history/get_bookings_history_success.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}