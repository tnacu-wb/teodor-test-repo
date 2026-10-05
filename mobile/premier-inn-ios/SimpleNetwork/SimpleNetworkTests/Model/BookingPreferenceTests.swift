//
//  BookingPreferenceTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 24/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class BookingPreferenceTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testUserBookingPreference() {

		let user = try? User(title: "Mr", firstName: "John", lastName: "Doe")
		XCTAssertNotNil(user)

		do {
			let bookingPrefDict: PIDictionary = ["wantSmsConfirmations": true]
			let data = try JSONSerialization.data(withJSONObject: bookingPrefDict, options: .prettyPrinted)

			let decoder = JSONDecoder()
			let bookingPreference = try? decoder.decode(BookingPreference.self, from: data)

			guard let wantConfirmations = bookingPreference?.wantSmsConfirmations else { return }

			XCTAssertTrue(wantConfirmations)
		} catch {
			XCTFail()
		}
	}

    
}
