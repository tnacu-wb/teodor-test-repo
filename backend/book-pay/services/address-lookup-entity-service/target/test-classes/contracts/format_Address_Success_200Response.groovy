package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get formatted address integration test")
    request {
        method 'GET'
        urlPath('/v1/addresses/GBX|ed2025d8-fd39-4d53-8a18-f65f233568ad|7.730BOGBXEgHjBwAAAAABAwEAAAAEL_2mkgAhEAYRAKEAAgAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTiAyVEQAAAAAAA--$8')
    }

    response {
        status 200
        body(file("response/format_Address_Success_200Response.json"))
        headers {
            header('Content-Type', 'application/json')
        }
    }
}