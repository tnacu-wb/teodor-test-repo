//
//  RestaurantTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 23/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class RestaurantTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testRestaurant() {

		XCTAssertThrowsError(try Restaurant(dictionary: ["Hi": "Oi"]))

		do {

			let restaurant = try Restaurant(dictionary: ["name": "Mac DOnald",
														 "description": "happy meals",
														 "image": "gar"])
			XCTAssert(restaurant.name == "Mac DOnald")
		} catch {
			XCTFail()
		}
	}
    
}
