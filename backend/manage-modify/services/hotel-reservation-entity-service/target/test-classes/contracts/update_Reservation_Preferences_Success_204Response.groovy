package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Update reservation preferences integration test")
    request {
        method 'PUT'
        urlPath('/v1/reservations/preferences')
        body('''
{
  "hotelId": "TestId",
  "reservationsIds": ["12345"],
  "preferencesCollections": [{
    "preferenceType": "EVENTS",
    "preferences": ["BDAY"]
  }]
}            ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 204
    }
}