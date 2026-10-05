//
//  FacilityTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 22/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class FacilityTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testFacility() {

		let facility2 = Facility(dictionary: ["description": "Ciao", "code": "some", "legend": "someLegend"])
		XCTAssertEqual(facility2.code, "some")
		XCTAssertEqual(facility2.description, "Ciao")
		XCTAssertEqual(facility2.legend, "someLegend")
	}
}
