//
//  FindReservationViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 06/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import Formeka
import SimpleNetwork
@testable import PremierInn

private class MockPresenter: FindReservationPresenterProtocol {
    
    var viewIsReadyDidCall = false
    var calendarButtonDidTapDidCall = false
    var submitButtonDidTapDidCall = false
    
    func viewIsReady() {
        
        viewIsReadyDidCall = true
    }
    
    func calendarButtonDidTap(date: Date) {
        
        calendarButtonDidTapDidCall = true
    }
    
    func submitButtonDidTap() {
        
        submitButtonDidTapDidCall = true
    }
    
    func calendarDidSelect(date: Date) {
        
        // This comes from the router
    }
}

class FindReservationViewTests: XCTestCase {
    
    private var view: FindReservationViewController!
    private var presenter: MockPresenter!
    
    override func setUp() {
        
        presenter = MockPresenter()
        
        view = FindReservationViewController()
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
    
    func testSubmitButton() {
        
        view.submitButtonDidTap(cell: FormekaSubmitButtonCell())
        XCTAssertTrue(presenter.submitButtonDidTapDidCall)
    }
    
}
