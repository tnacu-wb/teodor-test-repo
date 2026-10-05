package contracts.profile

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get companies profile integration test")
    request {
        method 'GET'
        urlPath('/v1/companies/profile') {
            queryParameters {
                parameter 'hotelId': 'MANOLD'
                parameter 'arNumber': '167003'
                parameter 'companyName': 'AMEROPA-REISEN G'
                parameter 'limit': 50
            }
        }
        headers {
            header('Content-Type', 'application/json;charset=UTF-8')
        }
    }

    response {
        status 200
        body('''
{
"companies": [
    {
    "name": "AMEROPA-REISEN GmbH",
    "telephoneNumber": "+1 415 555 0101",
    "profileType": "Company",
    "corpId": "AB-63",
    "companyId": "43215",
    "language": "en",
    "arNumber": "167003",
    "active": true,
    "negotiatedRateEnabled": false,
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
],
"totalResults": 1,
"hasMore": false,
"limit": 1,
"offset":0,
"page":0,
"pageSize":0
}
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}