//
//  MapContainerViewTests.swift
//  PremierInnTests
//
//  Created by Marcello Mascia on 02/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockPresenter: MapListContainerPresenterProtocol {
    

    var viewIsReadyDidCall = false
    var editButtonDidCall = false
    var mapButtonDidCall = false
    var backButtonDidCall = false
    var sortChangeDidCall = false
    var mapWillGoFullDidCall = false
    var mapDidGoFullDidCall = false
    var mapWillGoSmallDidCall = false
    var mapDidGoSmallDidCall = false
    var selectedLocationDidCall = false
    var selectedNightsDidCall = false
    var selectedGuestsDidCall = false
    var updatedSuggestionDidCall = false
    var criteriaUpdatedDidCall = false
    var condensedCriteriaTitleDidCall = false
    var nameOfLocationTextForCurrentCriteriaDidCall = false
    var roomsAndGuestsTextForCriteriaDidCall = false
    var dateTextForCurrentCriteriaSummaryDidCall = false
    
    var sortType: AvailabilitiesSorting = .distance
    var screenTitle: String?
    var screenSubtitle: String?
    
    var condensedCriteriaTitle: NSAttributedString {

        condensedCriteriaTitleDidCall = true

        return NSAttributedString(string: "")
    }

    var nameOfLocationTextForCurrentCriteria: String? {

        nameOfLocationTextForCurrentCriteriaDidCall = true

        return nil
    }
    
    var dateTextForCurrentCriteriaSummary: String? {

        dateTextForCurrentCriteriaSummaryDidCall = true

        return nil
    }

    var roomsAndGuestsTextForCriteria: String? {

        roomsAndGuestsTextForCriteriaDidCall = true

        return nil
    }

    func selectedLocation() {

        selectedLocationDidCall = true
    }

    func selectedNights() {

        selectedNightsDidCall = true
    }

    func selectedGuests() {

        selectedGuestsDidCall = true
    }

    func criteriaUpdated(to criteria: Criteria) {

        criteriaUpdatedDidCall = true
    }

    func updatedSuggestion(to suggestion: Suggestion) {

        updatedSuggestionDidCall = true
    }

	func viewIsReady() {

		viewIsReadyDidCall = true
	}

	func editButtonDidTap() {

		editButtonDidCall = true
	}

	func mapButtonDidTap() {

		mapButtonDidCall = true
	}

	func backButtonDidTap() {

		backButtonDidCall = true
	}
    
    func comingBackFromHotelDetails() { }

	func sortDidChange(with sortingRule: AvailabilitiesSorting) {

		sortChangeDidCall = true
	}

	func mapWillGoFullScreen() {

		mapWillGoFullDidCall = true
	}

	func mapDidGoFullScreen() {

		mapDidGoFullDidCall = true
	}

	func mapWillGoSmall() {

		mapWillGoSmallDidCall = true
	}

	func mapDidGoSmall() {

		mapDidGoSmallDidCall = true
	}

    func editGuestsButtonDidTap(sender: UIView) {
        
    }

    func editDatesButtonDidTap(sender: UIView) {
        
    }

    func editSuggestionButtonDidTap(sender: UIView) {
        
    }

    func selectedListCell() -> VenueCell? {

        return nil
    }

    func setPreselectedHotel(with code: String) {
        
    }

    func viewIsReady(shouldFetchNearbyHotels: Bool) {

        viewIsReadyDidCall = true
    }

    func presentErrorMessageOnListViewIfNeeded() {

    }
}

class MapContainerViewTests: XCTestCase {

	private var presenter: MockPresenter!
	private var controller: MapListContainerViewController!
	private let mockConstraint = NSLayoutConstraint()
	private let mockView = UIView()

	override func setUp() {
		super.setUp()

		presenter = MockPresenter()

		controller = MapListContainerViewController()
		controller.optionsViewTopHandle = mockConstraint
		controller.listTopHandle = mockConstraint
		controller.optionsView = mockView
		controller.presenter = presenter
	}

	override func tearDown() {

		presenter = nil
		controller = nil

		super.tearDown()
	}

    func testViewIsReady() {

        controller.loadView()
        controller.viewDidLoad()

        XCTAssertTrue(presenter.viewIsReadyDidCall)

        if UIDevice.current.userInterfaceIdiom != .pad {
            XCTAssertTrue(presenter.condensedCriteriaTitleDidCall)
            XCTAssertTrue(presenter.nameOfLocationTextForCurrentCriteriaDidCall)
            XCTAssertTrue(presenter.dateTextForCurrentCriteriaSummaryDidCall)
            XCTAssertTrue(presenter.roomsAndGuestsTextForCriteriaDidCall)
        }
    }

	func testButtons() {

		controller.backButtonTapped()
		XCTAssertTrue(presenter.backButtonDidCall)

		controller.mapButtonDidTap(UIButton())
		XCTAssertTrue(presenter.mapButtonDidCall)

		controller.sortCriteriaChanged(UISegmentedControl())
		XCTAssertTrue(presenter.sortChangeDidCall)

        controller.selectedLocation()
        XCTAssertTrue(presenter.selectedLocationDidCall)

        controller.selectedDate()
        XCTAssertTrue(presenter.selectedNightsDidCall)

        controller.selectedGuestsAndRooms()
        XCTAssertTrue(presenter.selectedGuestsDidCall)
	}

	func testFullScreen() {

		controller.makeMapFullScreen()

        // This is expected to be called immediately
        XCTAssertTrue(self.presenter.mapWillGoFullDidCall)
        
        // makeMapFullScreen performs a UI animation for .ocd length
        // so we wait for .ocd and then check the value
        wait(for: .ocd, description: #function)
        XCTAssertTrue(self.presenter.mapDidGoFullDidCall)
	}

	func testMapToSmall() {

		controller.makeMapSmall()

        // This is expected to be called immediately
        XCTAssertTrue(self.presenter.mapWillGoSmallDidCall)
        
        // makeMapSmall calls a function named hideCardList which animates
        // the UI for a length of .ocd
        // To test what happens after this delay we need to wait
        // wait(for:) will timeout anyway after 10 seconds of inactivity
        wait(for: .ocd, description: #function)
        
        XCTAssertTrue(self.presenter.mapDidGoSmallDidCall)
	}

    func testUpdateSuggestions() {

        controller.updatedSuggestion(to: PISuggestion(title: ""))
        XCTAssertTrue(presenter.updatedSuggestionDidCall)
    }

    func testCriteriaUpdate() {

        controller.updateCriteria(to: Criteria())
        XCTAssertTrue(presenter.criteriaUpdatedDidCall)
    }
}
