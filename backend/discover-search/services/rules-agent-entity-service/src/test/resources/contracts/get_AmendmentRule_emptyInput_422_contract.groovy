package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("rules-agent-controller")
    priority(1)
    request {
        method 'GET'
        urlPath('/v1/rules/amendments') {

            queryParameters {
                parameter 'rateType': ''
                parameter 'arrivalDate': '20220401'
                parameter 'hotelLocalDateTime' : '20200208T080910'
                parameter 'hotelCountryCode': 'GB'
            }
        }
    }

    response {
        status 422
        body(file("response/get_AmendmentRule_emptyInput_422_contract.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}