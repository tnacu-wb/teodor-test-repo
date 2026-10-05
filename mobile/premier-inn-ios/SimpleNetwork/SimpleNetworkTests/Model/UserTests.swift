//
//  UserTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 24/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class UserTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testUser() {

		let bookingDetails = BookingDetails.sharedInstance
		bookingDetails.reset()

		// User object
		XCTAssertThrowsError(try User(dictionary: ["bad" : "food"], sessionId: nil))
		XCTAssertThrowsError(try User(dictionary: ["account" : ["bad" : "food"]], sessionId: nil))
		XCTAssertThrowsError(try User(dictionary: ["account" : ["contactDetails" : ["bad" : "food"]]], sessionId: nil))

		XCTAssertNoThrow(try User(dictionary: ["contactDetail" : ["title" : "Mr", "firstName": "Pippo", "lastName": "Paperino"]], sessionId: nil))

		do {
			let paymentCardDict: PIDictionary = [
				"cardType": "VI",
				"cardNumber": "************1111",
				"expiryDate": "01/20",
				"cardHolderName": "Vasileios",
				"useExistingCard": false
			]
			let paymentPreferenceDict: PIDictionary = ["prepaymentRequired": false, "electronicInvoiceRequired": false, "paymentCard": paymentCardDict]

			let user = try User(dictionary: ["contactDetail" : ["title" : "Mr", "firstName": "Pippo", "lastName": "Paperino"], "paymentPreference": paymentPreferenceDict], sessionId: "fake-session")

			UserSessionManager.sharedInstance.loggedIn(with: user)
			XCTAssertNotNil(UserSessionManager.sharedInstance.currentUser)

		} catch {
			XCTFail("Expected user")
		}

		// Logout user
        Router.current = .uatGraphQL
        XCTAssertEqual(Router.current, .uatGraphQL)

		UserSessionManager.sharedInstance.userLoggedOut()
		XCTAssertNil(UserSessionManager.sharedInstance.currentUser)
	}
}
