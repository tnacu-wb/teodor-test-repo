package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("submit-application-exception")
    request {
        method 'POST'
        urlPath('/v1/pay-app/application/submit')
        body('''
                {
                    "applicationGuid": "invalid-guid",
                    "hostedPageGuid": "hosted-page-guid",
                    "hotelBookingRole": "hotel-booking-role",
                    "isDirectDebit": false,
                    "termsAndConditionAccepted": "Y",
                    "registrationQuestion": "registration-question",
                    "registrationAnswer": "registration-answer"
                }
                ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 400
        headers {
            header('Content-Type', 'application/json')
        }
    }
}