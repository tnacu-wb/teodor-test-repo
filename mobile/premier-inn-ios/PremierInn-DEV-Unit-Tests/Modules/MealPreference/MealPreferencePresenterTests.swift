//
//  MealPreferencePresenterTests.swift
//  PremierInnTests
//
//  Created by Freddie Parks on 08/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockInteractor: MealPreferenceInteractorInput {

    var mealPreferenceTracking: MealPreferenceTracking { return ("", "") }
    var numberOfRowsDidCall = false
    var numberOfSectionsDidCall = false
    var selectMealOptionDidCall = false
    var saveChangesDidCall = false

    func numberOfRows(forSection section: Int) -> Int {

        numberOfRowsDidCall = true
        return 1
    }

    func numberOfSections() -> Int {

        numberOfSectionsDidCall = true
        return 1
    }

    func allMealOptions() -> [MealOptionRowModel] {
        return []
    }

    func selectMealOption(option: MealOption) {
        selectMealOptionDidCall = true
    }

    func saveChanges() {
        saveChangesDidCall = true
    }
}

private class MockView: MealPreferenceViewInput {

    var parentNavigationController: UINavigationController? {
        return nil
    }

    var refreshRowsDidCall = false
    var processing = false
    var isProcessing: Bool {
        get {
            return processing
        }
        set {
            processing = newValue
        }
    }

    func refreshRows() {

        refreshRowsDidCall = true
    }
}

class MealPreferencePresenterTests: XCTestCase {

    // MARK: - Properties

    private var presenter: MealPreferencePresenter!
    private var interactor: MockInteractor!
    private var view: MockView!
    private var analytics: MockAnalyticsManager!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        presenter = MealPreferencePresenter()
        interactor = MockInteractor()
        view = MockView()

        presenter.interactor = interactor
        presenter.view = view

        analytics = MockAnalyticsManager()
        presenter?.analytics = analytics
    }

    override func tearDown() {

		presenter = nil
		interactor = nil
		view = nil

		super.tearDown()
    }

    // MARK: - Tests

    func testView_whenMealPreferenceUpdated_invokesTrackAction() {

        presenter?.mealPreferenceUpdated()

        let action = analytics.actions.first
        let userInfo = analytics.userInfos.first

        XCTAssertEqual(action, "iOS: Booking Preferences Changed")
        XCTAssertNotNil(userInfo as Any?)
    }

    func testNumberOfRows() {

        _ = presenter.numberOfRows(forSection: 0)
        XCTAssert(interactor.numberOfRowsDidCall == true)
    }

    func testNumberOfSections() {

        _ = presenter.numberOfSections()
        XCTAssert(interactor.numberOfSectionsDidCall == true)
    }

    func testSaveChanges() {
        presenter.saveChanges()
        XCTAssert(interactor.saveChangesDidCall == true)
        XCTAssert(view.isProcessing == true)
    }

    func testSelectMealOption() {
        presenter.selectMealOption(option: .premierInnBreakfast)
        XCTAssert(interactor.selectMealOptionDidCall == true)
    }

    func testReloadRows() {

        presenter.reloadRows()
        XCTAssert(view.refreshRowsDidCall == true)
    }
}
