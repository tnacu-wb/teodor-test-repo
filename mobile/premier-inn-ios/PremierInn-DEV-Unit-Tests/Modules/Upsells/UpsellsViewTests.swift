//
//  UpsellsViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 05/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockPresenter: UpsellsPresenterProtocol {
    
    var viewIsReadyDidCall = false
    var summaryButtonDidTapDidCall = false
    var selectedMealDidCall = false
    var allergyInformationDidTapDidCall = false
    var continueButtonDidTapDidCall = false
    
    func viewIsReady() {
        
        viewIsReadyDidCall = true
    }
    
    func summaryButtonDidTap() {
        
        summaryButtonDidTapDidCall = true
    }
    
    func selected(meal: UpsellItem?) {
        
        selectedMealDidCall = true
    }
    
    func allergyInformationDidTap() {
        
        allergyInformationDidTapDidCall = true
    }
    
    func continueButtonDidTap() {
        
        continueButtonDidTapDidCall = true
    }
}

class UpsellsViewTests: XCTestCase {
    
    private var view: UpsellsViewController!
    private var presenter: MockPresenter!
    
    override func setUp() {
        
        presenter = MockPresenter()
        
        view = UpsellsViewController()
        view.presenter = presenter
    }
    
    override func tearDown() {
        
        presenter = nil
        view = nil
        
        super.tearDown()
    }
    
    func testViewDidLoad() {
        
        view.viewDidLoad()
        XCTAssertTrue(presenter.viewIsReadyDidCall)
    }
    
    func testAnalyticsEvent() {
        
        XCTAssertEqual(view.customParameters[PIAnalytics.Keys.eventsString], "scOpen")
    }
}
