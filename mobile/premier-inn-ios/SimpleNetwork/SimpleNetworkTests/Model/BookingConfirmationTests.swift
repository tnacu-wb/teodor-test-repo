//
//  BookingConfirmationTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 22/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class BookingConfirmationTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testBookingConfirmation() {

        var testDict: [String: Any] = ["": ""]
        guard let dictData = try? JSONSerialization.data(withJSONObject: testDict, options: .prettyPrinted) else { return XCTFail("JSON serialization of bookingConfirmation dictionary failed") }
        XCTAssertNotNil(try? JSONDecoder().decode(BookingConfirmation.self, from: dictData))

        testDict = ["confirmationNumber": "TEST"]
        guard let dictData = try? JSONSerialization.data(withJSONObject: testDict, options: .prettyPrinted) else { return XCTFail("JSON serialization of bookingConfirmation dictionary failed") }
        XCTAssertNotNil(try? JSONDecoder().decode(BookingConfirmation.self, from: dictData))

        testDict = ["basketStatus": ["basketStatus": "FAILED", "basketError": ["code": "0", "description": "error"]], 
                    "confirmationNumber": "TEST"]
        guard let dictData = try? JSONSerialization.data(withJSONObject: testDict, options: .prettyPrinted) else { return XCTFail("JSON serialization of bookingConfirmation dictionary failed") }
        XCTAssertNotNil(try? JSONDecoder().decode(BookingConfirmation.self, from: dictData))
	}
    
}
