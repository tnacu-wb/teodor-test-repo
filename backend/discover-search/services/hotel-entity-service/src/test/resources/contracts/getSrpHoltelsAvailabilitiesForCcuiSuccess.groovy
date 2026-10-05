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
                parameter 'arrivalDate': "9999-01-10"
                parameter 'departureDate': "9999-01-25"
                parameter 'language': "en"
                parameter 'country': "gb"
                parameter 'adultsNumber': "1,1"
                parameter 'childrenNumber': "0,0"
                parameter 'roomTypes': "DB,SB"
                parameter 'page': 1
                parameter 'initialPageSize': 40
                parameter 'lazyLoadPageSize': 10
                parameter 'oldWorldChannel': "WEB"
                parameter 'subChannel': "WEB"
                parameter 'channel': "CCUI"
                parameter 'sort': "DISTANCE"
            }
        }
    }

    response {
        status 200
        body(file("response/get_srp_hotels_availabilities_ccui_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}