//
//  RoomsGuestsCriteriaInteractorTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 04/07/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

class RoomsGuestsCriteriaInteractorTests: XCTestCase {

	override func setUp() {
        super.setUp()

        UserSessionManager.sharedInstance.piUserLoggedOut()
    }
    
    override func tearDown() {

		super.tearDown()
    }
    
    func testUpdateAdultsNumber() {

		let criteria = Criteria()
		let interactor = RoomsGuestsCriteriaViewModel(criteria: criteria)

		interactor.updateAdultsNumber(value: 2, roomIndex: 0)
		XCTAssertEqual(interactor.criteria.rooms.first?.adults, 2)
	}

	func testUpdateChildrenNumber() {

		let criteria = Criteria()
		let interactor = RoomsGuestsCriteriaViewModel(criteria: criteria)

		interactor.updateChildrenNumber(value: 2, roomIndex: 0)
		XCTAssertEqual(interactor.criteria.rooms.first?.children, 2)
	}

	func testUpdateCotValue() {

		let criteria = Criteria()
		let interactor = RoomsGuestsCriteriaViewModel(criteria: criteria)

		try? interactor.updateCotValue(true, roomIndex: 0)
		XCTAssertEqual(interactor.criteria.rooms.first?.cotRequired, true)

        try? interactor.updateRoomType(.accessible, roomIndex: 0)
        try? interactor.updateCotValue(true, roomIndex: 0)
        XCTAssert(interactor.criteria.rooms[0].cotRequired == false)
	}

	func testUpdateRoomType() {

		let criteria = Criteria()
        criteria.rooms[0].cotRequired = true

		let interactor = RoomsGuestsCriteriaViewModel(criteria: criteria)

		try? interactor.updateRoomType(.accessible, roomIndex: 0)
		XCTAssertEqual(interactor.criteria.rooms.first?.type, .accessible)
        XCTAssert(interactor.criteria.rooms[0].cotRequired == false)
	}

	func testAppendRoom() {

		let criteria = Criteria()
		let interactor = RoomsGuestsCriteriaViewModel(criteria: criteria)

		XCTAssertNoThrow(try interactor.appendRoom())
		XCTAssertEqual(interactor.criteria.rooms.count, 2)
	}

	func testRemoveRoomAtIndex() {

		var criteria = Criteria()
		criteria.rooms = [Room(), Room(), Room(), Room()]
		let interactor = RoomsGuestsCriteriaViewModel(criteria: criteria)

		XCTAssertNoThrow(try interactor.removeRoomAtIndex(2))
		XCTAssertEqual(interactor.criteria.rooms.count, 3)
	}

	func testRemoveRoomAtIndex_TooFew() {

		var criteria = Criteria()
		criteria.rooms = [Room()]
		let interactor = RoomsGuestsCriteriaViewModel(criteria: criteria)

		XCTAssertThrowsError(try interactor.removeRoomAtIndex(2), "") { (error) in
			XCTAssertEqual(error.localizedDescription, PILocalizedString("minRoomsNumberReached", comment: "Error message showed when the minimum number of rooms limit is reached"))
		}
		XCTAssertEqual(interactor.criteria.rooms.count, 1)
	}

    func testAppendRoom_CallUs() {
        SettingsManager.sharedInstance.restrictionsArray = [Restrictions(maxRooms: 4, maxArrivalDateCount: 1, maxNights: 1, maxRoomsAmend: 4, channel: .PI)]
        var criteria = Criteria()
        criteria.rooms = [Room(), Room(), Room(), Room()]
        let interactor = RoomsGuestsCriteriaViewModel(criteria: criteria)

        XCTAssertThrowsError(try interactor.appendRoom(), "") { (error) in
            let error = error as! AddRoomError
            XCTAssertEqual(error.title, "Unable to add more rooms")
            XCTAssertEqual(error.message, "For a group booking of 5 to 9 rooms, call us on 0333 003 8101.")
            XCTAssertEqual(error, AddRoomError.callUs)
        }
        XCTAssertEqual(interactor.criteria.rooms.count, 4)
    }

    func testAppendRoom_GoToWeb() {
        SettingsManager.sharedInstance.restrictionsArray = [Restrictions(maxRooms: 9, maxArrivalDateCount: 1, maxNights: 1, maxRoomsAmend: 4, channel: .PI)]
        var criteria = Criteria()
        criteria.rooms = [Room(), Room(), Room(), Room(), Room(), Room(), Room(), Room(), Room()]
        let interactor = RoomsGuestsCriteriaViewModel(criteria: criteria)
        XCTAssertThrowsError(try interactor.appendRoom(), "") { (error) in
            let error = error as! AddRoomError
            XCTAssertEqual(error.title, "Unable to add more rooms")
            XCTAssertEqual(error.message, "To book 10 rooms or more, please visit our website to complete a group booking form and we’ll be in touch.")
            XCTAssertEqual(error, AddRoomError.goToWeb)
        }
        XCTAssertEqual(interactor.criteria.rooms.count, 9)
    }
}
