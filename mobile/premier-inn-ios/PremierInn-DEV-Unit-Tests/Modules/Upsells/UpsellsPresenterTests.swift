//
//  UpsellsPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 05/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest

import SimpleNetwork
@testable import PremierInn

private class MockView: UpsellsViewProtocol {
    
    var loadViewModelDidCall = false
    
    func loadViewModel(with: BookingDetails) {
        
        loadViewModelDidCall = true
    }
}

private class MockRouter: UpsellsRouterProtocol {
    
    var showSummaryDidCall = false
    var showAllergyInformationDidCall = false
    var continueToNextStepDidCall = false
    
    func showSummary(with bookingDetails: BookingDetails) {
        
        showSummaryDidCall = true
    }
    
    func showAllergyInformation() {
        
        showAllergyInformationDidCall = true
    }
    
    func continueToNextStep(with bookingDetails: BookingDetails) {
        
        continueToNextStepDidCall = true
    }
}

class UpsellsPresenterTests: XCTestCase {
    
    private var view: MockView!
    private var presenter: UpsellsPresenter!
    private var router: MockRouter!
    
    override func setUp() {
        
        view = MockView()
        router = MockRouter()
        
        presenter = UpsellsPresenter(with: BookingDetails())
        presenter.view = view
        presenter.router = router
    }
    
    override func tearDown() {
        
        presenter = nil
        view = nil
        router = nil
        
        super.tearDown()
    }
    
    func testViewIsReady() {
        
        presenter.viewIsReady()
        XCTAssertTrue(view.loadViewModelDidCall)
    }
    
    func testSummaryButton() {
        
        presenter.summaryButtonDidTap()
        XCTAssertTrue(router.showSummaryDidCall)
    }
    
    func testSelectedMeal() {
        
        presenter.selected(meal: nil)
        XCTAssertTrue(view.loadViewModelDidCall)
    }
    
    func testAllergyInformation() {
        
        presenter.allergyInformationDidTap()
        XCTAssertTrue(router.showAllergyInformationDidCall)
    }
    
    func testContinueButton() {
        
        presenter.continueButtonDidTap()
        XCTAssertTrue(router.continueToNextStepDidCall)
    }
}
