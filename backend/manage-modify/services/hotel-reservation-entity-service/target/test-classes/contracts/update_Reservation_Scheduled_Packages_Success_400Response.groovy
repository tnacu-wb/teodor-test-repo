package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Update reservation scheduled packages integration test")
    request {
        method 'PUT'
        urlPath('/v1/reservations/ancillaries/scheduled')
        body('''
{
  "hotelId": "TestId",
  "reservations": [
    {
      "reservationsId": "12345",
          "addPackages": [
            {
                "id": "PIBTEST",
                "noOfSelections": 1,
                "scheduledDates": [
                    "2021-08-20"
                ]
            }
          ],
          "removePackages": [
            {
                "id": "PIBTEST",
                "noOfSelections": 1
            }
          ]
    }
  ]
}              ''')
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