package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Hotel Facilities cache update trigger")
    request {
        method 'GET'
        urlPath('/v1/content/hotels/facilities/updater/trigger')
    }

    response {
        status 200
    }
}