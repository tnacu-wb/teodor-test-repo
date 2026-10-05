//
//  PlanTripInfoRouterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 07/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest

@testable import PremierInn

private class MockDelegate: PlanYourTripInfoRouterDelegate {

    var openDirectionsDidCall = false

    func openDirections(withSender sender: UIView) {

        openDirectionsDidCall = true
    }
}

class PlanYourTripInfoRouterTests: XCTestCase {

    fileprivate var mockDelegate: MockDelegate?

    var router: PlanYourTripInfoRouter?

    override func setUp() {
        super.setUp()

        mockDelegate = MockDelegate()

        router = PlanYourTripInfoRouter()
        router?.delegate = mockDelegate
    }

    override func tearDown() {
        super.tearDown()
    }

    func testOpenDirections() {

        router?.openDirections(withSender: UIView())

        XCTAssert(mockDelegate?.openDirectionsDidCall == true)
    }
}
