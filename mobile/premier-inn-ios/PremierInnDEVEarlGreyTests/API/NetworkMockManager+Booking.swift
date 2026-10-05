//
//  NetworkMockManager+Booking.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 06/05/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import Hippolyte

extension NetworkMockManager {

    static func holdBookingStubRequest() {

        guard let url = URL(string: "https://api-uat.whitbread.co.uk/booking/hotels") else {

            print("Unable to create URL with string: https://api-uat.whitbread.co.uk/booking/hotels")
            return
        }

        let response = StubResponse.Builder()
            .stubResponse(withStatusCode: 200)
            .addBody(NetworkMockManager.dataFromJsonMock(fileName: "holdBookingResponse")!)
            .build()

        let request = StubRequest.Builder()
            .stubRequest(withMethod: .POST, url: url)
            .addResponse(response)
            .build()

        Hippolyte.shared.add(stubbedRequest: request)
    }
}
