//
//  CriteriaTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 22/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class CriteriaTests: XCTestCase {

	override func setUp() {
		super.setUp()
		// Put setup code here. This method is called before the invocation of each test method in the class.
	}

	override func tearDown() {
		// Put teardown code here. This method is called after the invocation of each test method in the class.
		super.tearDown()
	}

	func testCriteria() {

		var criteria = Criteria()
		criteria.nights = 2
		criteria.arrivalDate = Date()
		criteria.rooms = [Room()]

		XCTAssert(criteria.adultsCount == 1)
		XCTAssert(criteria.childrenCount == 0)
		XCTAssert(criteria.guestsCount == 1)

//		XCTAssert(criteria.leadDays == 0)
		if let checkoutDate = Date().dateByAddingUnit(unitType: .day, number: criteria.nights), let criteriaCheckoutDate = criteria.checkOutDate {
			XCTAssert(criteriaCheckoutDate.analyticsDateFormat == checkoutDate.analyticsDateFormat)
		}
//		XCTAssertFalse(criteria.adultsCountDescription.isEmpty)
//		XCTAssertFalse(criteria.childrenCountDescription.isEmpty)
//		XCTAssertFalse(criteria.rateDatesSummary.isEmpty)
//		XCTAssertFalse(criteria.reviewDatesSummary.isEmpty)
//		XCTAssertFalse(criteria.reviewDatesSummaryShort.isEmpty)
	}

	func testCriteria_ArrivalDateInThePast() {

		var criteria = Criteria()
		criteria.nights = 2
		criteria.arrivalDate = Date.distantPast
		criteria.rooms = [Room()]

		let currentCalendar = Calendar.current
		let dateComponents = currentCalendar.dateComponents([.day, .month, .year], from: Date())

		let today = currentCalendar.date(from: dateComponents)!

		XCTAssertGreaterThanOrEqual(criteria.arrivalDate, today)
	}

	func testRoomRules() {

		let criteria = Criteria()
		let room = criteria.rooms.first
		room?.adults = 1
		room?.children = 0
		room?.type = .double

		XCTAssertEqual(room?.type.code, "DB")

		room?.adults = 2
		XCTAssertEqual(room?.type.code, "DB")

		room?.children = 1
		XCTAssertEqual(room?.type.code, "FAM")

		room?.children = 0
		XCTAssertEqual(room?.type.code, "DB")

		room?.type = .twin
		room?.adults = 1
		XCTAssertEqual(room?.type.code, "DB")

		room?.children = 1
		XCTAssertEqual(room?.type.code, "FAM")

		room?.children = 2
		XCTAssertEqual(room?.type.code, "FAM")
	}
}
