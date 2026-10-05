//
//  MealPreferenceViewTests.swift
//  PremierInnTests
//
//  Created by Freddie Parks on 08/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
import Formeka
@testable import PremierInn

private class MockPresenter: MealPreferencePresenterInput {

    var mealPreferenceTracking: MealPreferenceTracking { return ("", "") }
    var reloadRowsDidCall = false
    var numberOfRowsDidCall = false
    var numberOfSectionsDidCall = false
    var selectMealOptionDidCall = false
    var saveChangesDidCall = false

    func reloadRows() {

        reloadRowsDidCall = true
    }

    func numberOfRows(forSection section: Int) -> Int {

        numberOfRowsDidCall = true
        return 1
    }

    func numberOfSections() -> Int {

        numberOfSectionsDidCall = true
        return 1
    }

    func selectMealOption(option: MealOption) {
        selectMealOptionDidCall = true
    }

    func allMealOptions() -> [MealOptionRowModel]? {
        return nil
    }

    func saveChanges() {
        saveChangesDidCall = true
    }
}

class MealPreferenceViewTests: XCTestCase {

    fileprivate var presenter: MockPresenter!
    var controller: MealPreferenceView!

    override func setUp() {
        super.setUp()

        presenter = MockPresenter()

        controller = MealPreferenceView()
        controller.presenter = presenter
    }

    override func tearDown() {

        presenter = nil
        controller = nil

        super.tearDown()
    }

    func testReloadRows() {

        controller.viewWillAppear(false)
        XCTAssert(presenter.reloadRowsDidCall == true)
    }

    func testNumberOfSections() {

        _ = controller.numberOfSections(in: UITableView())
        XCTAssert(presenter.numberOfSectionsDidCall == true)
    }

    func testSaveChanges() {
        let submitButton: FormekaSubmitButtonCell = FormekaSubmitButtonCell.fromNib()!
        controller.submitButtonDidTap(cell: submitButton)
        XCTAssert(presenter.saveChangesDidCall == true)
    }
}
