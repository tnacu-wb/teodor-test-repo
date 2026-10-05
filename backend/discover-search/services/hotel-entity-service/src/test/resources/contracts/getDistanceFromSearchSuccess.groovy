package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get distance from search")
    request {
        method 'GET'
        urlPath('v1/hotels/LONEUS/distance') {
            queryParameters {
                parameter 'location': "ChIJdd4hrwug2EcRmSrV3Vo6llI"
                parameter 'locationFormat': "managedPlaceId"
                parameter 'radius': 20
                parameter 'radiusUnit': "mi"
            }

        }
    }

    response {
        status 200
        body(file("response/get_distance_from_search_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}