package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get formatted address using an incorrect moniker integration test")
    request {
        method 'GET'
        urlPath('/v1/addresses/GBX|incorrectMoniker|Id')
    }

    response {
        status 400
        body(file("response/format_Address_IncorrectMonikerId.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
