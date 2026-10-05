package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("rules-agent-controller")
    priority(1)
    request {
        method 'GET'
        urlPath('/v1/rules/room-substitutions') {
            queryParameters {
                parameter 'adults': '2'
                parameter 'children': '0'
                parameter 'pms': 'OP'
                parameter 'roomType': ''
            }
        }
    }

    response {
        status 422
        body(file("response/get_RoomSubstitutionRule_emptyInput_422_contract.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}