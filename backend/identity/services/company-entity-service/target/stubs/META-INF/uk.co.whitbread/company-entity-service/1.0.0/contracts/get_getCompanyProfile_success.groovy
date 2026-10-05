package contracts.profile

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get company profile integration test")
    request {
        method 'GET'
        urlPath('/v1/companies/123455') {
        }
        headers {
            header('Content-Type', 'application/json;charset=UTF-8')
        }
    }

    response {
        status 200
        body('''
{
    "name": "AMEROPA-REISEN GmbH",
    "telephoneNumber": "+1 415 555 0101",
    "profileType": "Company",
    "corpId": "AB-63",
    "companyId": "123455",
    "language": "en",
    "arNumber": "167003",
    "active": true,
    "negotiatedRateEnabled": true,
    "restricted": true,
    "restrictedReason": "A2CNACTV",
    "address": {
        "addressLine1": "Siemensstraße 27",
        "addressLine2": "Siemensstraße 27",
        "addressLine3": "Siemensstraße 27",
        "addressLine4": "Siemensstraße 27",
        "city": "Bad Homburg",
        "country": "Romania",
        "postalCode": "61352"
        }    
}
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}