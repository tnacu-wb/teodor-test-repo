package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("meals-entity-controller")
    request {
        method 'GET'
        urlPath('/v1/content/meals') {
            queryParameters {
                parameter 'country': 'gb'
                parameter 'language': 'en'
                parameter 'hotelId': ''
            }
        }
    }

    response {
        status 422
        body('''
        {
            "errCode": "401",
            "globalErrTextTemplate": "validation.error.form",
            "details": {
                "elementId": "hotelId",
                "errTextTemplate": "must not be empty"
              }
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}