//
//  RoomTests.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

class RoomTests: XCTestCase {

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    func testAvailableRoomTypes() {

        let room = Room()

        XCTAssertEqual(room.availableRoomTypes().first?.code, "DB")

        room.adults = 2

        XCTAssertEqual(room.availableRoomTypes().first?.code, "DB")

        room.children = 1

        XCTAssertEqual(room.availableRoomTypes().first?.code, "FAM")
    }

    func testAvailableRoomTypes_FunnyNumbers() {

        let room = Room()
        room.adults = 234
        room.children = 321

        XCTAssertEqual(room.availableRoomTypes(), [.family])
    }
}
