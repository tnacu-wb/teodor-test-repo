//
//  CiolUpsellDetailsViewTests.swift
//  PremierInn
//
//  Created by Oltean Vasile Bogdan on 24.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

class MockCiolUpsellDetailsEventHandler: CiolUpsellDetailsViewEventHandler {
    var viewIsReadyCalled = false
    var reloadViewModelCalled = true
    var showAllergyCalled = false
    var showMenuCalled = false
    var didCallDidUpdateUpsell = false
    var didCallSendUpselloOutput = false

    func viewIsReady() {
        viewIsReadyCalled = true
    }
    
    func reloadViewModel() {
        reloadViewModelCalled = true
    }

    func showAllergyInfo() {
        showAllergyCalled = true
    }
    
    func showMenu() {
        showMenuCalled = true
    }

    func didUpdateUpsell(with id: String, action: CIOLStepperAction, completion: @escaping () -> ()) {
        didCallDidUpdateUpsell = true
    }

    func sendUpsellOutput(action: PremierInn.CiolUpsellDetailsOutputAction) {
        didCallSendUpselloOutput = true
    }
}

class CiolUpsellDetailsViewControllerTests: XCTestCase {
    
    var viewController: CiolUpsellDetailsViewController!
    var mockEventHandler: MockCiolUpsellDetailsEventHandler!

    override func setUp() {
        super.setUp()
        mockEventHandler = MockCiolUpsellDetailsEventHandler()
        viewController = CiolUpsellDetailsViewController()
        viewController.eventHandler = mockEventHandler
    }

    override func tearDown() {
        viewController = nil
        mockEventHandler = nil
        super.tearDown()
    }

    func testViewWillAppearCallsViewIsReady() {
        viewController.viewWillAppear(true)
        XCTAssertTrue(mockEventHandler.viewIsReadyCalled, "viewIsReady() should be called when viewDidLoad() is executed")
    }
    
    func testReloadViewModelCalled() {
        viewController.eventHandler?.reloadViewModel()
        XCTAssertTrue(mockEventHandler.reloadViewModelCalled, "reloadViewModel() should be called")
    }
    
    func testShowAllergyCalled() {
        viewController.eventHandler?.showAllergyInfo()
        XCTAssertTrue(mockEventHandler.showAllergyCalled, "showAllergyInfo() should be called")
    }

    func testShowMenuCalled() {
        viewController.eventHandler?.showMenu()
        XCTAssertTrue(mockEventHandler.showMenuCalled, "showMenu() should be called")
    }

    func testDidUpdateUpsell() {
        viewController.eventHandler?.didUpdateUpsell(with: "", action: .didAdd(1), completion: {})
        XCTAssertTrue(mockEventHandler.didCallDidUpdateUpsell, "didUpdateUpsell should be called")
    }

    func testSendOutput() {
        viewController.eventHandler?.sendUpsellOutput(action: .add)
        XCTAssertTrue(mockEventHandler.didCallSendUpselloOutput, "sendUpsellOutput should be called")
    }
}
