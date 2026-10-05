//
//  PaymentCardTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 23/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class PaymentCardTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testPaymentCard() {

		XCTAssertThrowsError(try PaymentCard(dictionary: ["bad": "food"]))
		XCTAssertThrowsError(try PaymentCard(dictionary: ["cardNumber": "4539798517443207"]))
		XCTAssertThrowsError(try PaymentCard(dictionary: ["cardNumber": "4539798517443207", "expiryDate" : "bad date"]))
		XCTAssertThrowsError(try PaymentCard(dictionary: ["cardNumber": "4539798517443207", "expiryDate" : "2017-12-01 12:00:00"]))
		XCTAssertThrowsError(try PaymentCard(dictionary: ["cardNumber": "4539798517443207", "expiryDate" : "2017-12-01 12:00:00", "cardType" : "AC"]))
		XCTAssertThrowsError(try PaymentCard(dictionary: ["cardNumber": "4539798517443207", "expiryDate" : "2017-12-01 12:00:00", "cardType" : "AC", "cardName": "Mastercard Credit"]))
		XCTAssertThrowsError(try PaymentCard(dictionary: ["cardNumber": "4539798517443207", "expiryDate" : "2017-12-01 12:00:00", "cardName": "Mastercard Credit", "cardholderName" : "John Doe", "feeAmount" : 2.0, "feeCurrency" : "£"]))
		XCTAssertNotNil(try PaymentCard(dictionary: ["cardNumber": "4539798517443207", "expiryDate" : "2017-12-01 12:00:00", "cardType" : "AC", "cardName": "Mastercard Credit", "cardholderName" : "John Doe", "feeAmount" : 2.0, "feeCurrency" : "£"]))
		XCTAssertNotNil(try PaymentCard(dictionary: ["cardholderName": "Ms Sadsa Asdasd", "cardNumber": "11111111", "cardName": "Loyalty", "cardType" : "LY", "expiryDate": "11/2019"]))
		XCTAssertNotNil(try PaymentCard(dictionary: ["cardholderName": "Ms Sadsa Asdasd", "cardNumber": "11111111", "cardName": "Loyalty", "cardType" : "LY", "expiryDate": "11/2019", "startDate": "10/2017"]))
        XCTAssertNotNil(try PaymentCard(dictionary: ["cardholderName": "Ms Sadsa Asdasd", "cardNumber": "11111111", "cardName": "Loyalty", "cardType" : "LY", "expiryDate": "11/19", "startDate": "10/17"]))
        XCTAssertNotNil(try PaymentCard(dictionary: ["cardholderName": "Ms Sadsa Asdasd", "cardNumber": "11111111", "cardName": "Loyalty", "cardType" : "LY", "expiryDate": "1119", "startDate": "1017"]))

		// let card2 = PaymentCard(cardNumber: "111", expiryDate: Date(), cardholderName: "John Doe", cardName: "America Express", cardCode: "AM")

		do {
			let card3 = try PaymentCard(dictionary: ["cardNumber": "4539798517443207", "expiryDate" : "2017-12-01 12:00:00", "cardType" : "VI", "cardName": "Mastercard Credit", "cardholderName" : "John Doe", "feeAmount" : 2.0, "feeCurrency" : "£"])
			XCTAssertEqual(card3.cardNumberMasked, "**** **** **** 3207")
			XCTAssertTrue(card3.expired(onDate: Date.distantFuture))
			XCTAssertEqual(card3, card3)
		} catch {
			XCTFail("Expecting card3")
		}
	}

}
