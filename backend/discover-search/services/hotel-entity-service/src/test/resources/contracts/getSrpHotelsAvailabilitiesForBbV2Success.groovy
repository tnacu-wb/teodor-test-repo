
package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get Search Results Page Hotel Availabilities from search")
    request {
        method 'GET'
        urlPath('v2/hotels/availabilities') {
            queryParameters {
                parameter 'location': "ChIJs3uVVC1Ad0gRDh8qK7iLtUE"
                parameter 'locationFormat': "PLACEID"
                parameter 'radius': 20
                parameter 'radiusUnit': "MILES"
                parameter 'arrivalDate': "2030-06-10"
                parameter 'departureDate': "2030-06-24"
                parameter 'language': "en"
                parameter 'country': "gb"
                parameter 'adultsNumber': "2,1,2"
                parameter 'childrenNumber': "0,0,0"
                parameter 'roomTypes': "DB,SB,DB"
                parameter 'page': 1
                parameter 'initialPageSize': 40
                parameter 'lazyLoadPageSize': 10
                parameter 'oldWorldChannel': "CBT"
                parameter 'subChannel': "WEB"
                parameter 'channel': "BB"
                parameter 'sort': "DISTANCE"
            }
        }
        headers {
            header('WB-Authorization',"Bearer dummy_value")
        }
    }

    response {
        status 200
        body(file("response/get_srp_hotels_availabilities_bb_v2_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}

