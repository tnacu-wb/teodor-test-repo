//
//  BusinessAccountTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 22/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class BusinessAccountTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testBusinessAccount() {

		XCTAssertThrowsError(try BusinessAccount(dictionary: nil))

		if let businessAccount = try? BusinessAccount(dictionary: ["breakfastCode": "17", "dinnerAllowance": "26.49", "atosUsername": 1, "atosPassword": true]) {

			XCTAssert(businessAccount.breakfastCode == "17")
			XCTAssertEqual(businessAccount.dinnerAllowance, "26.49")

			let dictionary = businessAccount.dictionary
			XCTAssertNil(dictionary["atosUsername"])
			XCTAssertNil(dictionary["atosPassword"])
			XCTAssertNotNil(dictionary["breakfastCode"])
			XCTAssertNotNil(dictionary["dinnerAllowance"])

			let dinner = dictionary["dinnerAllowance"] as? PIDictionary
			XCTAssertEqual(dinner?["amount"] as? NSNumber, 26.49)
			XCTAssertEqual(dinner?["currency"] as? String, "GBP")
		}

		if let businessAccount = try? BusinessAccount(dictionary: ["breakfastCode": "17", "dinnerAllowance": "asdasd", "atosUsername": 1, "atosPassword": true]) {

			let dictionary = businessAccount.dictionary
			let dinner = dictionary["dinnerAllowance"] as? PIDictionary
			XCTAssertEqual(dinner?["amount"] as? NSNumber, 0)
		}
	}
    
}
