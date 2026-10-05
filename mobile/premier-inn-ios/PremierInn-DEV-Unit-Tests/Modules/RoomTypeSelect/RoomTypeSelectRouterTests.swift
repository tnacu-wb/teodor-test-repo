//
//  RoomTypeSelectRouterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 25/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import SimpleNetwork
import XCTest

@testable import PremierInn

private class MockDelegate: RoomTypeSelectRouterDelegate {

    var roomTypeSelectedDidCall = false

    func roomTypeSelectViewControllerDidUpdate(roomType: SelectableRoomType) {

        roomTypeSelectedDidCall = true
    }
}

class RoomTypeSelectRouterTests: XCTestCase {

    fileprivate var mockDelegate: MockDelegate?

    var router: RoomTypeSelectRouter?


    override func setUp() {
        super.setUp()

        mockDelegate = MockDelegate()
        router = RoomTypeSelectRouter(with: UIViewController(), and: mockDelegate, andShouldBePopped: true)
    }

    override func tearDown() {
        super.tearDown()
    }

    func testRoomTypeSelected() {

        router?.selected(roomType: RoomType.double)

        XCTAssert(mockDelegate?.roomTypeSelectedDidCall == true)
    }
}
