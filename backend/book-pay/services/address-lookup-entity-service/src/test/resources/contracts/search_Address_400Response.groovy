package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Search address by postcode integration test")
    request {
        method 'GET'
        urlPath('/v1/addresses') {
            queryParameters {
                parameter 'countryCode': 'gb'
            }
        }
    }

    response {
        status 422
        body('''
        {
            "errCode": "401",
            "globalErrTextTemplate":"validation.error.form",
            "details":[
                {
                  "elementId":"searchTerm",
                  "errTextTemplate":"must not be blank"
                }
            ]
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}