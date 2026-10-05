package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Update booking allowances")
    request {
        method 'PUT'
        urlPath $(consumer(regex('/v1/baskets/[a-zA-Z]{3}-' + uuid() + '/allowances')), producer("/v1/baskets/LON-7b97e3f6-dce9-4572-b3e1-a5aa91ebc4b7/allowances"))
        body([
                bookingAllowances : [[
                        allowance : $(consumer(anyNonBlankString())),
                        budget : $(consumer(regex("^(\\d+\\.\\d+)\$")), producer(100.0))
                ]]
        ])
        headers {
            header('Content-Type', 'application/json')
            header('If-Match', "1684426938941")
        }
    }

    response {
        status 204
    }
}