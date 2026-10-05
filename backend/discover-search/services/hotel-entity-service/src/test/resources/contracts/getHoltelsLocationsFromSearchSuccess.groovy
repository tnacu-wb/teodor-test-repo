package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get distance from search")
    request {
        method 'GET'
        urlPath('v1/hotels/locations') {
            queryParameters {
                parameter 'location': "ChIJs3uVVC1Ad0gRDh8qK7iLtUE"
                parameter 'locationFormat': "placeId"
                parameter 'radius': 20
                parameter 'radiusUnit': "mi"
            }
        }
    }

    response {
        status 200
        body(file("response/get_hotels_locations_from_search_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}