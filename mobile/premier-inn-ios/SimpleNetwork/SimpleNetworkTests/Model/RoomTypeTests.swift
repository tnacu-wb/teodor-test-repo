//
//  RoomTypeTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 23/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class RoomTypeTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testRoomType() {

		XCTAssertEqual(RoomType.single.availableTypes(adults: 0, children: 0, cot: false), [])
		XCTAssertEqual(RoomType.single.availableTypes(adults: 1, children: 1, cot: false), [.family])
		XCTAssertEqual(RoomType.single.availableTypes(adults: 1, children: 2, cot: false), [.family])
		XCTAssertEqual(RoomType.single.availableTypes(adults: 1, children: 3, cot: false), [.family])
		XCTAssertEqual(RoomType.single.availableTypes(adults: 2, children: 0, cot: false), [.double, .twin, .accessible])
		XCTAssertEqual(RoomType.single.availableTypes(adults: 3, children: 0, cot: false), [.double, .twin, .accessible])
        XCTAssertEqual(RoomType.single.availableTypes(adults: 1, children: 0, cot: true), [.single, .double, .accessible])
		XCTAssertEqual(RoomType.single.availableTypes(adults: 1, children: 0, cot: false), [.single, .double, .accessible])
		XCTAssertEqual(RoomType.single.availableTypes(adults: 1, children: 1, cot: true), [.family])

		XCTAssertEqual(RoomType.accessible.availableTypes(adults: 1, children: 0, cot: false), [.accessible, .double, .single])

		XCTAssertEqual(RoomType.twin.availableTypes(adults: 2, children: 0, cot: false), [.twin, .double, .accessible])
		XCTAssertEqual(RoomType.accessible.availableTypes(adults: 2, children: 0, cot: false), [.accessible, .double, .twin])		
	}

}
