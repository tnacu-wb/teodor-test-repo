package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get Search Results Page Hotel Availabilities from search")
    request {
        method 'GET'
        urlPath('v1/hotels/availabilities') {
            queryParameters {
                parameter 'location': "ChIJs3uVVC1Ad0gRDh8qK7iLtUE"
                parameter 'locationFormat': "PLACEID"
                parameter 'radius': 20
                parameter 'radiusUnit': "MILES"
                parameter 'arrivalDate': "{{now format='yyyy-MM-dd'}}"
                parameter 'departureDate': "{{now offset='4 days' format='yyyy-MM-dd'}}"
                parameter 'language': "en"
                parameter 'country': "gb"
                parameter 'adultsNumber': "2,1,2"
                parameter 'childrenNumber': "0,0,0"
                parameter 'roomTypes': "DB,SB,DB"
                parameter 'page': 1
                parameter 'initialPageSize': 40
                parameter 'lazyLoadPageSize': 10
                parameter 'oldWorldChannel': "WEB"
                parameter 'subChannel': "WEB"
                parameter 'channel': "PI"
                parameter 'sort': "DISTANCE"
            }
        }
    }

    response {
        status OK()
        body(file("response/get_srp_hotels_availabilities_search_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}