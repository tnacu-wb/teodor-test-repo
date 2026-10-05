//
//  WebserviceRequestsTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 08/06/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

private class MockRouter: Router {

    let service = MockService(scheme: "", host: "")

    override func service(for action: WebserviceAction) throws -> WebserviceProtocol {

        return service
    }
}

private class MockService: Webservice, WebserviceProtocol {

    var holdBookingDidCall = false

    func holdBooking(bookingDetails: BookingDetails, sensorData: String) throws -> Resource<String> {

        holdBookingDidCall = true

        throw WebserviceError.notImplemented(#function)
    }
}

class WebserviceRequestsTests: XCTestCase {

    private var mockRouter: MockRouter!

    override func setUp() {
        super.setUp()

        mockRouter = MockRouter()
    }
    
    override func tearDown() {

        Router.current = .production
        mockRouter = nil

        super.tearDown()
    }
    
    func testHoldBooking() {

        Router.current = mockRouter

        let manager = RequestsManager()
        manager.holdBooking(bookingDetails: BookingDetails(), sensorData: "") { (sessionId, error) in

        }

        XCTAssertTrue(mockRouter.service.holdBookingDidCall)
    }

}
