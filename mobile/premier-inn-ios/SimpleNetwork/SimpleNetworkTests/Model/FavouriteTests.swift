//
//  FavouriteTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 22/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class FavouriteTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testFavourites() {

		let ud = UserDefaults.standard

		let manager = SimpleStorageManager<Favourite>(dataSource: ud)
		manager.reset()

		XCTAssertEqual(manager.items.count, 0)

		let favourite1 = try! Favourite(dictionary: ["identifier": "testIdentifier1"])
		let favourite2 = try! Favourite(dictionary: ["identifier": "testIdentifier2"])
		let favourite3 = try! Favourite(dictionary: ["identifier": "testIdentifier3"])

		try? manager.add(favourite1)
		try? manager.add(favourite2)
		try? manager.add(favourite3)

		XCTAssertEqual(manager.items.count, 3)
		XCTAssertEqual(manager.items.first?.identifier, "testIdentifier1")
		XCTAssertEqual(manager.items.last?.identifier, "testIdentifier3")

		// Test duplicates
		let favourite4 = try! Favourite(dictionary: ["identifier": "testIdentifier3"])

		try? manager.add(favourite4)

		XCTAssertEqual(manager.items.count, 3)

		// Test remove
		_ = manager.remove(favourite1)

		XCTAssertEqual(manager.items.count, 2)
		XCTAssertEqual(manager.items.first?.identifier, "testIdentifier2")
		XCTAssertEqual(manager.items.last?.identifier, "testIdentifier3")
	}
    
}
