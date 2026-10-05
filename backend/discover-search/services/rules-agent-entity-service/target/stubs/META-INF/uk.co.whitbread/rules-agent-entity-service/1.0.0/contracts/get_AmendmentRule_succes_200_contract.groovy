package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("rules-agent-controller")
    priority(1)
    request {
        method 'GET'
        urlPath('/v1/rules/amendments') {

            queryParameters {
                parameter 'rateType': "Flex"
                parameter 'arrivalDate': "20220401"
                parameter 'hotelLocalDateTime' : "20220208T080910"
                parameter 'hotelCountryCode': "GB"

            }
        }
    }

    response {
        status 200
        body(file("response/get_AmendmentRule_success_200_contract.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}