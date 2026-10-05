//
//  RoomTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 23/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class RoomTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testRoom() {

		let dailyRates = [
			["date" : "", "price": ["amount" : 12.3, "currency" : "GBP"]]
		]
        let room = Room(dictionary: ["adults" : 2,
                                     "children" : 2, "cotRequired" : true,
                                     "lettingType" : "ASDASD",
                                     "totalCost" : ["amount" : 12.0, "currency" : "GBP"],
                                     "dailyRates" : dailyRates,
                                     "guest": ["title": "Mr", "firstName": "First", "lastName": "Guest"],
                                     "accompanyingGuest": ["title": "Mr", "firstName": "Second", "lastName": "Guest"]])
		XCTAssertEqual(room.adults, 2)
		XCTAssertEqual(room.children, 2)
		XCTAssertEqual(room.cotRequired, true)
		XCTAssertNotNil(room.dailyRates)
        XCTAssertNotNil(room.leadGuest)
        XCTAssertNotNil(room.accompanyingGuest)
	}

    func testCopyingRoomCorrectlyCopiesByValueAndNotReference() {

        let existingRoomWithLotsOfAdults = Room()
        existingRoomWithLotsOfAdults.adults = 100

        guard let newRoomWithOneAdult = existingRoomWithLotsOfAdults.copy() as? Room else {
            
            XCTFail()
            return
        }

        newRoomWithOneAdult.adults = 1

        XCTAssertNotEqual(existingRoomWithLotsOfAdults.adults, newRoomWithOneAdult.adults)
    }

}
