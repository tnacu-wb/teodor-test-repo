//
//  CostTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 22/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class CostTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testCost() {

		let cost = Cost(amount: 12.34, currencyCode: "EUR", locale: Locale(identifier: "en_GB"))
		let cost2 = Cost(amount: 12.34, currencyCode: "GBP", locale: Locale(identifier: "it_IT"))
		let cost3 = Cost(amount: 12.34, currencyCode: "£", locale: Locale(identifier: "it_IT"))

		let dictionary: PIDictionary = ["cardNumber": "4539798517443207", "expiryDate" : "2017-12-01 12:00:00", "cardType" : "AC", "cardName": "Mastercard Credit", "cardholderName" : "John Doe", "feeAmount" : 2.0, "feeCurrency" : "£"]
		let cost4 = try? Cost(dictionary: dictionary, locale: Locale(identifier: "it_IT"))

		XCTAssertEqual(cost.localizedValue, "€12.34")
		XCTAssertEqual(cost2.localizedValue, "£12.34")
		XCTAssertEqual(cost3.localizedValue, "£12.34")
		XCTAssertEqual(cost4?.localizedValue, "£2.00")
	}

    func testCost_BrokenDict() {

        let dictionary: PIDictionary = ["feeAmountasdasd" : 2.0, "feeCurrencyasdas" : "£"]

        XCTAssertThrowsError(try Cost(dictionary: dictionary, locale: Locale(identifier: "it_IT")))
    }

    func testCost2() {

        let dictionary: PIDictionary = ["amount" : 2.19, "currency" : "£"]
        let cost = try! Cost(dictionary: dictionary, locale: Locale(identifier: "it_IT"))

        XCTAssertEqual(cost.localizedValue, "£2.19")

        let dictionary2: PIDictionary = ["feeAmount" : "2.19", "currency" : "£"]
        let cost2 = try! Cost(dictionary: dictionary2, locale: Locale(identifier: "it_IT"))

        XCTAssertEqual(cost2.localizedValue, "£2.19")
    }

    func testCostSum() {

		let cost = Cost(amount: 12.34, currencyCode: "EUR", locale: Locale(identifier: "en_GB"))
		let cost2 = Cost(amount: 4.12, currencyCode: "GBP", locale: Locale(identifier: "it_IT"))
		let cost3 = Cost(amount: 3.34, currencyCode: "£", locale: Locale(identifier: "it_IT"))

		XCTAssertNil(cost + cost2)

		let sum = cost2 + cost3
		XCTAssertEqual(sum?.amount, 7.46)
		XCTAssertEqual(sum?.currencyCode, "GBP")
	}

    func testCostSubtraction() {

        let cost = Cost(amount: 12.34, currencyCode: "EUR", locale: Locale(identifier: "en_GB"))
        let cost2 = Cost(amount: 4.12, currencyCode: "GBP", locale: Locale(identifier: "it_IT"))
        let cost3 = Cost(amount: 3.34, currencyCode: "£", locale: Locale(identifier: "it_IT"))

        XCTAssertNil(cost - cost2)

        let sum = cost2 - cost3
        XCTAssertEqual(sum?.amount, 0.78)
        XCTAssertEqual(sum?.currencyCode, "GBP")
    }

    func testCostEquality() {

        let cost = Cost(amount: 12.34, currencyCode: "EUR", locale: Locale(identifier: "en_GB"))
        let cost2 = Cost(amount: 12.34, currencyCode: "GBP", locale: Locale(identifier: "it_IT"))
        let cost3 = Cost(amount: 12.34, currencyCode: "£", locale: Locale(identifier: "it_IT"))

        XCTAssertNotEqual(cost, cost2)
        XCTAssertEqual(cost2, cost3)
        XCTAssertNotEqual(cost, cost3)
    }

    func testCostComparison() {

        let cost = Cost(amount: 12.34, currencyCode: "EUR")
        let cost2 = Cost(amount: 12.35, currencyCode: "EUR")
        let cost3 = Cost(amount: 12.36, currencyCode: "EUR")

        XCTAssertGreaterThan(cost2, cost)
        XCTAssertGreaterThan(cost3, cost2)
        XCTAssertGreaterThan(cost3, cost)
    }

    func testCostDecodable() {

        let jsonString = """
        {
            "amount": "12.34",
            "currency": "EUR"
        }
        """

        let jsonData = jsonString.data(using: .utf8)!
        let decoder = JSONDecoder()
        let cost = try! decoder.decode(Cost.self, from: jsonData)

        XCTAssertEqual(cost.amount, 12.34)
        XCTAssertEqual(cost.currencyCode, "EUR")
    }

    func testCostDecodable2() {

        let jsonString = """
        {
            "amount": 12.34,
            "currency": "EUR"
        }
        """

        let jsonData = jsonString.data(using: .utf8)!
        let decoder = JSONDecoder()
        let cost = try! decoder.decode(Cost.self, from: jsonData)

        XCTAssertEqual(cost.amount, 12.34)
        XCTAssertEqual(cost.currencyCode, "EUR")
    }

    func testCostDecodable_Error() {

        let jsonString = """
        {
            "amountasdasdsa": 12.34,
            "currency": "EUR"
        }
        """

        let jsonData = jsonString.data(using: .utf8)!
        let decoder = JSONDecoder()

        XCTAssertThrowsError(try decoder.decode(Cost.self, from: jsonData))
    }

    func testCostDecodable_Error2() {

        let jsonString = """
        {
            "amount": 12.34,
            "currencyasdasdasd": "EUR"
        }
        """

        let jsonData = jsonString.data(using: .utf8)!
        let decoder = JSONDecoder()

        XCTAssertThrowsError(try decoder.decode(Cost.self, from: jsonData))
    }
}
