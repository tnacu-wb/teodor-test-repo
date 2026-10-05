//
//  AddressTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 22/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class AddressTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

	private var goodDictionary: [String: String] = [
		"addressline1": "Royal Victoria Dock",
		"addressline2": "2 Festoon Way",
		"addressline3": "London",
		"countryCode": "GB",
		"postcode": "E16 1SJ"
	]
    
	func testAddress() {

		XCTAssertThrowsError(try Address(dictionary: nil)) { error in
			XCTAssertEqual(error as? AddressError, AddressError.missingAddressDictionary)
		}
		XCTAssertNoThrow(try Address(dictionary: goodDictionary))

		let address = try? Address(dictionary: goodDictionary)
		XCTAssertEqual(address?.description, "Royal Victoria Dock, 2 Festoon Way, London, E16 1SJ")

	}

	func testAddressPostalAddress() {

		let address = try? Address(dictionary: goodDictionary)

		// This test will fail if running < iOS 10
		XCTAssertNotNil(address?.postalAddressDictionary)
	}

	func testAddressEquality() {

		let address = try? Address(dictionary: goodDictionary)

		XCTAssertEqual(address, address)
	}
}
