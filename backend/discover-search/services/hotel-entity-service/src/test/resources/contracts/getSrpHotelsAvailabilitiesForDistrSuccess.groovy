package contracts

import org.springframework.cloud.contract.spec.Contract

import java.time.LocalDate

Contract.make {
    description("Get Search Results Page Hotel Availabilities from search")
    request {
        method 'POST'
        urlPath('v2/hotels/availabilities/distr')
        body(
            bookingChannel: [
                    channel: "DISTR",
                    subchannel: "WEB",
                    language: "en",
            ],
            hotelIds: [
                    "LONEUS",
                    "BERALX" 
            ],
            arrivalDate: $(consumer(anyDate()), producer(LocalDate.now().toString())),
            departureDate: $(consumer(anyDate()), producer(LocalDate.now().plusDays(3).toString())),
            rooms: [
                    [
                        tag: "DB",
                        adults: 2,
                        children: 0,
                        numberOfRooms: 3
                    ],
                    [
                        tag: "FAM",
                        adults: 2,
                        children: 1,
                        numberOfRooms: 1
                    ]
            ],
            rates: [
                ratePlanCodes: [
                 ]
            ]
        )
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(file("response/get_srp_hotels_availabilities_distr_v2_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}

