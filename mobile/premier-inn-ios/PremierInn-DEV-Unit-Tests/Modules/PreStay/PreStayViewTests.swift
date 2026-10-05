//
//  PreStayViewTests.swift
//  PremierInn
//
//  Created by Florin Velesca on 26.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

class MockPreStayEventHandler: PreStayViewEventHandler {
   
    var viewIsReadyCalled = false
    var isUpdateViewModelCalled = false
    var isShowEditDetailsCalled = false
    var continueButtonTappedCalled = false
    var isHandleSelectOccasionCalled = false
    var isSpecialOccasionOnCalled = false

    func viewIsReady() {
        viewIsReadyCalled = true
    }

    func handleContinueButtonTap() {
        continueButtonTappedCalled = true
    }
    
    func showEditDetails(flow: EditDetailsFlow) {
        isShowEditDetailsCalled = true
    }
    
    func updateViewModel(with: EditDetailsModel) {
        isUpdateViewModelCalled = true
    }
    
    func handleSelectOccasion() {
        isHandleSelectOccasionCalled = true
    }
    
    func isSpecialOccasionOn(_ isOn: Bool) {
        isSpecialOccasionOnCalled = true
    }
    

}

class PreStayViewControllerTests: XCTestCase {
    
    var viewController: PreStayViewController!
    var mockEventHandler: MockPreStayEventHandler!

    override func setUp() {
        super.setUp()
        mockEventHandler = MockPreStayEventHandler()
        viewController = PreStayViewController()
        viewController.eventHandler = mockEventHandler
    }

    override func tearDown() {
        viewController = nil
        mockEventHandler = nil
        super.tearDown()
    }

    func testViewDidLoadCallsViewIsReady() {
        viewController.viewDidLoad()
        XCTAssertTrue(mockEventHandler.viewIsReadyCalled, "viewIsReady() should be called when viewDidLoad() is executed")
    }
    
    func testShowEditDetails_Successful() {
        mockEventHandler.showEditDetails(flow: .leadGuestTitleNameInfo(indexPath: IndexPath()))
    
        XCTAssertTrue(mockEventHandler.isShowEditDetailsCalled)
    }
    
    func test_UpdateViewModel_Successful() {
        mockEventHandler.updateViewModel(with: EditDetailsModel(flow: .leadGuestTitleNameInfo(indexPath: IndexPath()),
                                                                title: "",
                                                                firstName: "",
                                                                lastName: ""))
        XCTAssertTrue(mockEventHandler.isUpdateViewModelCalled)
    }

    func test_handleSelectOccasion_Successful() {
        mockEventHandler.handleSelectOccasion()
        XCTAssertTrue(mockEventHandler.isHandleSelectOccasionCalled)
    }

    func test_specialOccasionOnCalled_Successful() {
        mockEventHandler.isSpecialOccasionOn(true)
        XCTAssertTrue(mockEventHandler.isSpecialOccasionOnCalled)
    }
}
