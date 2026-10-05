package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("reservations-manager-entity-controller")
    request {
        method 'GET'
        headers {
            header('Content-Type', 'application/json')
            header('WB-Authorization', 'Bearer test==')
        }
        urlPath('/v1/bookings/history') {
            queryParameters {
                parameter 'business': false
                parameter 'companyId': 'companyId'
                parameter 'employeeId': 'employeeId'
                parameter 'filterValue': 'filterValue'
                parameter 'includeCheckInBookings': false
                parameter 'continuationToken': 'continuationToken'
                parameter 'channe;': "pi"
                parameter 'subchannel': "WEB"
            }
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