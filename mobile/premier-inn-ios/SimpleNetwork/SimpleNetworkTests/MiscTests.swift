//
//  MiscTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 24/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

func getDictFor(file: String, in className: AnyClass) -> PIDictionary {
    let fileURL = Bundle.module.url(forResource: file, withExtension: "json")!
    let data = try! Data(contentsOf: fileURL)
    let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
    return jsonDictionary["data"] as! PIDictionary
}

class MiscTests: XCTestCase {

	override func setUp() {
		super.setUp()
		// Put setup code here. This method is called before the invocation of each test method in the class.
	}

	override func tearDown() {
		// Put teardown code here. This method is called after the invocation of each test method in the class.
		super.tearDown()
	}

	func testDictionaryExtension() {

		let dict: PIDictionary = ["key1": 2, "key2": "Hello"]

		let value: Int? = dict.value(forKeys: ["key1", "key2"])
		XCTAssertEqual(value, 2)
		let value2: String? = dict.value(forKeys: ["key1", "key2"])
		XCTAssertEqual(value2, "Hello")
		let value3: Int? = dict.value(forKeys: ["asda", "adasda"])
		XCTAssertNil(value3)
	}

    func testDateFormatterWithDatesInMonth() {

        let arrivalDate = DateFormatter.parameterFormatter.date(from: "2021-10-19")
        let departureDate = DateFormatter.parameterFormatter.date(from: "2021-10-26")

        XCTAssertEqual(arrivalDate?.numberOfNights(to: departureDate), 7)
    }

    func testDateFormatterWithDatesInDifferentMonths() {

        let arrivalDate = DateFormatter.parameterFormatter.date(from: "2021-08-29")
        let departureDate = DateFormatter.parameterFormatter.date(from: "2021-09-05")

        XCTAssertEqual(arrivalDate?.numberOfNights(to: departureDate), 7)
    }

    func testDateFormatterWithFallTimeChange() {

        let arrivalDate = DateFormatter.parameterFormatter.date(from: "2021-10-29")
        let departureDate = DateFormatter.parameterFormatter.date(from: "2021-11-05")

        XCTAssertEqual(arrivalDate?.numberOfNights(to: departureDate), 7)
    }

    func testDateFormatterWithSpringTimeChange() {

        let arrivalDate = DateFormatter.parameterFormatter.date(from: "2022-03-25")
        let departureDate = DateFormatter.parameterFormatter.date(from: "2022-04-01")

        XCTAssertEqual(arrivalDate?.numberOfNights(to: departureDate), 7)
    }

	func testNumberOfNights() {

		let startDate = Date()
		let endDate = startDate.addingTimeInterval(60 * 60 * 24 * 4)

		XCTAssertEqual(startDate.numberOfNights(to: endDate), 4)
	}

	func testDaysToCheckInDate_SameDay() {

		let searchDate: Date = {
			var components = DateComponents()
			components.year = 2017
			components.month = 9
			components.day = 12

			components.hour = 1
			components.minute = 0
			components.second = 0

			return Calendar.current.date(from: components)!
		}()

		let checkInDate: Date = {
			var components = DateComponents()
			components.year = 2017
			components.month = 9
			components.day = 12

			components.hour = 23
			components.minute = 59
			components.second = 59

			return Calendar.current.date(from: components)!
		}()

		XCTAssertEqual(searchDate.daysToCheckInDate(checkInDate), 0)
	}

	func testDaysToCheckInDate_NextDay() {

		let searchDate: Date = {
			var components = DateComponents()
			components.year = 2017
			components.month = 9
			components.day = 12

			return Calendar.current.date(from: components)!
		}()

		let checkInDate: Date = {
			var components = DateComponents()
			components.year = 2017
			components.month = 9
			components.day = 13

			components.hour = 23
			components.minute = 59
			components.second = 59

			return Calendar.current.date(from: components)!
		}()

		XCTAssertEqual(searchDate.daysToCheckInDate(checkInDate), 1)
	}
}
