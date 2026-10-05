//
//  PISuggestionTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 22/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import CoreLocation
@testable import SimpleNetwork

class PISuggestionTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testSuggestion() {

		let suggestion1 = PISuggestion(coordinate: CLLocationCoordinate2D(latitude: 0, longitude: 0))
		let suggestion2 = PISuggestion(coordinate: CLLocationCoordinate2D(latitude: 1, longitude: 1))
		let suggestion4 = PISuggestion(dictionary: ["name" : "A test", "lat" : 12.34, "long" : 45.67, "isHotel" : true])
		let suggestion5 = PISuggestion(dictionary: ["name" : "B test", "location" : ["latitude" : 12.34, "longitude" : 45.67]])
		let suggestion6 = PISuggestion(dictionary: ["name" : "A test", "lat" : 12.34, "long" : 45.67, "isHotel" : true])
		suggestion6.rangeOfSearchTerm = NSRange(location: 2, length: 2)
		let suggestion7 = PISuggestion(dictionary: ["name" : "A test", "lat" : 12.34, "long" : 45.67, "isHotel" : true])
		suggestion7.rangeOfSearchTerm = NSRange(location: 3, length: 2)
		let suggestion8 = PISuggestion(dictionary: ["name" : "B test", "lat" : 12.34, "long" : 45.67, "isHotel" : true])
		suggestion8.rangeOfSearchTerm = NSRange(location: 3, length: 2)

		XCTAssertTrue(suggestion1 == suggestion2)
		XCTAssertNotEqual(suggestion1, suggestion2)
		XCTAssertGreaterThan(suggestion5, suggestion4)
		XCTAssertGreaterThan(suggestion7, suggestion6)
		XCTAssertGreaterThan(suggestion8, suggestion7)
		XCTAssertNil(suggestion4.subtitle)
		XCTAssertNotNil(suggestion4.title)
		XCTAssertNotNil(suggestion4.iconName)
		XCTAssertNotNil(suggestion1.iconName)
		XCTAssertNotNil(suggestion1.coordinate)
	}
    
}
