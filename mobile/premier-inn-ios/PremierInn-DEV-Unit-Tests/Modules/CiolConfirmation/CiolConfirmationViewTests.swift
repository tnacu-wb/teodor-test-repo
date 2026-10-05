//
//  CiolConfirmationViewTests.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

class MockCiolConfirmationPresenter: CiolConfirmationPresenterProtocol {
   
    var isViewDidLoadCalled = false
    var isNavigateToMyBookingsCalled = false
    var isViewInstructionsCalled = false
    var isRefreshStaysCalled = false
    
    func viewIsReady() {
        isViewDidLoadCalled = true
    }
    
    func navigateToMyBookings() {
        isNavigateToMyBookingsCalled = true
    }
    
    func showKeyInstructions() {
        isViewInstructionsCalled = true
    }
    
    func refreshStays() {
        isRefreshStaysCalled = true
    }

    func trackCiolComplete() {}
}

class CiolConfirmationViewTests: XCTestCase {
    var viewController: CiolConfirmationViewController!
    var mockPresenter: MockCiolConfirmationPresenter!
    
    override func setUp() {
        super.setUp()
        
        mockPresenter = MockCiolConfirmationPresenter()
        viewController = CiolConfirmationViewController()
        viewController.presenter = mockPresenter
        
    }
    
    func testViewDidLoad_CallsPresenterViewDidLoad() {
        viewController.viewDidLoad()
        XCTAssertTrue(mockPresenter.isViewDidLoadCalled, "viewDidLoad should call presenter's viewDidLoad")
    }
    
    func testNavigateToRoot_CallsNavigateToRoot() {
        viewController.gotItAction()
        XCTAssertTrue(mockPresenter.isRefreshStaysCalled, "gotItAction should call presenter's refreshStays")
        XCTAssertTrue(mockPresenter.isNavigateToMyBookingsCalled, "gotItAction should call presenter's navigateToMyBookings")
    }
    
    func testShowKeyInstructions() {
        viewController.showKeyInstructions()
        XCTAssertTrue(mockPresenter.isViewInstructionsCalled, "showKeyInstructions should call presenter's showKeyInstructions")
    }
}
