//
//  DebugMenuTests.swift
//  PremierInn
//
//  Created by Marcello Mascia on 29/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import XCTest

@testable import PremierInn

class DebugMenuTests: XCTestCase {

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    func testLocationExceptions() {

		let debugManager = DebugManager.sharedInstance
		let locationManager = LocationManager()

		// Make sure that the original implementation is called
		locationManager.findUserLocation()
		XCTAssertNotNil(locationManager.coreLocationManager.delegate)

		// Override
		debugManager.overrideLocationManager(true)

		// Make sure that the new implementation is called
		locationManager.findUserLocation()
		XCTAssertNil(locationManager.coreLocationManager.delegate)

		debugManager.overrideLocationManager(false)

		// Make sure that the original implementation is restored
		locationManager.findUserLocation()
		XCTAssertNotNil(locationManager.coreLocationManager.delegate)
    }

}
