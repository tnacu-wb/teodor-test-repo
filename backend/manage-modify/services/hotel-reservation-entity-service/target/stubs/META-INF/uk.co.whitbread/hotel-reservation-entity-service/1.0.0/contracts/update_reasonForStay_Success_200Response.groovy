package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Update reason for stay integration test")
    request {
        method 'PUT'
        urlPath('/v1/reservations/reasonForStay')
        body('''
             {
               "hotelId":"TestId",
               "reasonForStay":"LEI",
               "basketReference":"TestId1234567"
             }
            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("update_reasonForStay_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}