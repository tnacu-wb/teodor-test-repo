//
//  RoomsGuestsCriteriaPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 03/07/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockInteractor: RoomsGuestsCriteriaInteractorProtocol {

	var appendRoomDidCall = false
	var removeRoomAtIndexDidCall = false
	var updateAdultsNumberDidCall = false
	var updateChildrenNumberDidCall = false
	var updateCotValueDidCall = false
	var criteriaDidCall = false
	var updateRoomTypeDidCall = false
    var shouldShowAddRoomSection = false

    var infantCustomerServicesModel: CustomerServicesModel {
        return ("", "", "")
    }

    var cotInAccessibleCustomerServicesModel: CustomerServicesModel {
        return ("", "", "")
    }

	var criteria: Criteria {
		criteriaDidCall = true

		return Criteria()
	}

    func infants(for section: Int) -> Int {
        return 0
    }

    func updateInfants(_ value: Int, roomIndex: Int) {

    }

	func updateAdultsNumber(value: Int, roomIndex: Int) {

		updateAdultsNumberDidCall = true
	}

	func updateChildrenNumber(value: Int, roomIndex: Int) {

		updateChildrenNumberDidCall = true
	}

	func updateCotValue(_ value: Bool, roomIndex: Int) {

		updateCotValueDidCall = true
	}

	func updateRoomType(_ type: RoomType, roomIndex: Int) {

		updateRoomTypeDidCall = true
	}

	func removeRoomAtIndex(_ index: Int) throws {

		removeRoomAtIndexDidCall = true
	}

    func appendRoom() throws -> Room {

		appendRoomDidCall = true

		return Room()
	}

}

private class MockView: RoomsGuestsCriteriaViewProtocol {

	var reloadDidCall = false
	var refreshConfirmButtonDidCall = false
	var appendRoomSectionDidCall = false
	var reloadVisibleHeadersDidCall = false
	var removeSectionDidCall = false
	var callCustomerServiceDidCall = false
	var showErrorDidCall = false
	var showRoomTypeControllerDidCall = false

	func appendRoomSection(with room: Room, index: Int) {

		appendRoomSectionDidCall = true
	}

	func reloadVisibleHeaders() {

		reloadVisibleHeadersDidCall = true
	}

	func refreshConfirmButton(with title: NSAttributedString?) {

		refreshConfirmButtonDidCall = true
	}

	func removeSection(at index: Int) {

		removeSectionDidCall = true
	}

	func showError(_ error: Error, at indexPath: IndexPath) {

		showErrorDidCall = true
	}

    func callCustomerServices(phoneNumber: String, title: String, message: String) {
        
    }

	func reload() {

		reloadDidCall = true
	}

	func callCustomerService(phoneNumber: String) {

		callCustomerServiceDidCall = true
	}

	func showRoomTypeController(for room: Room) {

		showRoomTypeControllerDidCall = true
	}

    func showAddAdditionalRoomErrorAlert(with controller: UIAlertController) {
        
    }

	func provideSmallHapticFeedback() {

	}

	func provideErrorHapticFeedback() {

	}
}

class RoomsGuestsCriteriaPresenterTests: XCTestCase {

	private var presenter: RoomsGuestsCriteriaPresenter!
	private var interactor: MockInteractor!
	private var view: MockView!

    override func setUp() {
        super.setUp()

		interactor = MockInteractor()

		view = MockView()

		presenter = RoomsGuestsCriteriaPresenter(interactor: interactor)
		presenter.view = view
    }
    
    override func tearDown() {

		presenter = nil
		interactor = nil
		view = nil

		super.tearDown()
    }
    
    func testViewIsReady() {

		presenter.viewIsReady()
		XCTAssertTrue(view.reloadDidCall)
		XCTAssertTrue(view.refreshConfirmButtonDidCall)
	}

	func testAddRoomButtonDidTap() {

		presenter.addRoomButtonDidTap()
		XCTAssertTrue(interactor.appendRoomDidCall)
		XCTAssertTrue(view.appendRoomSectionDidCall)
		XCTAssertTrue(view.reloadVisibleHeadersDidCall)
		XCTAssertTrue(view.refreshConfirmButtonDidCall)
	}

	func testDeleteRoomButtonDidTap() {

		presenter.deleteRoomButtonDidTap(at: 1)
		XCTAssertTrue(view.removeSectionDidCall)
		XCTAssertTrue(interactor.removeRoomAtIndexDidCall)
		XCTAssertTrue(view.reloadVisibleHeadersDidCall)
		XCTAssertTrue(view.refreshConfirmButtonDidCall)
	}

	func testCallCustomerServiceButtonDidTap() {

		presenter.callCustomerServiceButtonDidTap()
		XCTAssertTrue(view.callCustomerServiceDidCall)
	}

	func testNumberOfAdultsDidChange() {

		presenter.numberOfAdultsDidChange(value: 2, at: IndexPath(item: 0, section: 0))
		XCTAssertTrue(interactor.updateAdultsNumberDidCall)
		XCTAssertTrue(view.reloadDidCall)
		XCTAssertTrue(view.refreshConfirmButtonDidCall)
	}

	func testNumberOfChildrenDidChange() {

		presenter.numberOfChildrenDidChange(value: 1, at: IndexPath(item: 0, section: 0))
		XCTAssertTrue(interactor.updateChildrenNumberDidCall)
		XCTAssertFalse(interactor.updateCotValueDidCall)
		XCTAssertTrue(view.reloadDidCall)
		XCTAssertTrue(view.refreshConfirmButtonDidCall)
	}

	func testNumberOfChildrenDidChange_NoChildren() {

		presenter.numberOfChildrenDidChange(value: 0, at: IndexPath(item: 0, section: 0))
		XCTAssertTrue(interactor.updateChildrenNumberDidCall)
		XCTAssertTrue(view.reloadDidCall)
		XCTAssertTrue(view.refreshConfirmButtonDidCall)
	}

	func testCotValueDidChange() {

		presenter.cotValueDidChange(value: true, at: IndexPath(item: 0, section: 0))
		XCTAssertTrue(interactor.updateCotValueDidCall)
		XCTAssertTrue(view.reloadDidCall)
		XCTAssertTrue(view.refreshConfirmButtonDidCall)
	}

	func testNumberOfPeopleNotAllowed() {

		presenter.numberOfPeopleNotAllowed(at: IndexPath(item: 0, section: 0), error: AddressError.missingAddressDictionary)
		XCTAssertTrue(view.showErrorDidCall)
	}

	func testRoomTypeButtonDidSelect() {

		presenter.roomTypeButtonDidSelect(at: IndexPath(item: 0, section: 0))
		XCTAssertTrue(interactor.criteriaDidCall)
		XCTAssertTrue(view.showRoomTypeControllerDidCall)
	}

	func testRoomTypeDidSelect() {

		presenter.roomTypeButtonDidSelect(at: IndexPath(item: 0, section: 0))
		presenter.roomTypeDidSelect(RoomType.double)
		XCTAssertTrue(interactor.criteriaDidCall)
		XCTAssertTrue(interactor.updateRoomTypeDidCall)
		XCTAssertTrue(view.reloadDidCall)
		XCTAssertTrue(view.refreshConfirmButtonDidCall)
	}
}
