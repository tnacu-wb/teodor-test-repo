//
//  RestaurantMenuItemTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 23/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class RestaurantMenuItemTests: XCTestCase {

    override func setUp() {
        super.setUp()

    }

    override func tearDown() {

        super.tearDown()
    }

    func testRestaurantMenuItem() {

        let item = RestaurantMenuItem(name: "Full English Breakfast", description: "Not <b>Vegetarian</b>", disclaimer: "Due to government advice, we have taken the decision to close this restaurant until further notice", path: "")

        XCTAssertEqual(item.title, "Full English Breakfast")
        XCTAssertEqual(item.body, "Not Vegetarian")
        XCTAssertEqual(item.disclaimer, "Due to government advice, we have taken the decision to close this restaurant until further notice")
    }

}
