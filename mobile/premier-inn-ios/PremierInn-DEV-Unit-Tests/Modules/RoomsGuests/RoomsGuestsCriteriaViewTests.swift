//
//  RoomsGuestsCriteriaViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 04/07/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

class RoomsGuestsCriteriaViewTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
        UserSessionManager.sharedInstance.piUserLoggedOut()
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testExample() {

		let room = Room()
		room.adults = 2
		room.children = 1
		room.cotRequired = true

		var criteria = Criteria()
		criteria.rooms = [room, Room()]

		let interactor = RoomsGuestsCriteriaViewModel(criteria: criteria)

		let controller = RoomsGuestsCriteriaView()
		controller.presenter = RoomsGuestsCriteriaPresenter(interactor: interactor)
		controller.loadViewModel()

		XCTAssertEqual(controller.viewModel?.sections.count, 3)

		let roomSection = controller.viewModel?.sections.first
		XCTAssertNotNil(roomSection)

		let roomSection2 = controller.viewModel?.sections[1]
		XCTAssertNotNil(roomSection2)
		XCTAssertEqual(roomSection2?.rows.count, 4) // Adult, Children, Infants, Room Type
	}

}
