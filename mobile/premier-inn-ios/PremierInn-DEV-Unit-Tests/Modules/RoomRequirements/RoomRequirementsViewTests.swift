//
//  RoomRequirementsViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 18/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import Formeka
@testable import PremierInn

private class MockPresenter: RoomRequirementsPresenterInput {
    
    var viewIsReadyDidCall = false
    var saveChangesDidCall = false
    var cotRequirementChangedDidCall = false
    var adultRequirementsChangedDidCall = false
    var childrenRequirementsChangedDidCall = false
    
    var roomRequirementsTracking: RoomRequirementsTracking { return ("", "") }
    
    func viewIsReady() {
        
        viewIsReadyDidCall = true
    }
    
    func saveChanges() {
        
        saveChangesDidCall = true
    }
    
    func roomTypeDidSelect() {
        
    }
    
    func cotRequirementChanged(toRequired: Bool) {
        
        cotRequirementChangedDidCall = true
    }
    
    func adultRequirementsChanged(to: Int) {
        
        adultRequirementsChangedDidCall = true
    }
    
    func childrenRequirementsChanged(to: Int) {
        
        childrenRequirementsChangedDidCall = true
    }
}

class RoomRequirementsViewTests: XCTestCase {
    
    private var view: RoomRequirementsView!
    private var presenter: MockPresenter!
    
    override func setUp() {
        
        presenter = MockPresenter()
        
        view = RoomRequirementsView()
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
        XCTAssertTrue(presenter.saveChangesDidCall)
    }
    
    func testCotChanges() {
        
        view.cotSwitchDidChange(cell: CotSelectorCell())
        XCTAssertTrue(presenter.cotRequirementChangedDidCall)
    }
    
    func testAdultsSelectorChanges() {
        
        view.customStepperCellDidChangeValue(cell: AdultsSelectorCell(), value: 1)
        XCTAssertTrue(presenter.adultRequirementsChangedDidCall)
    }
    
    func testChildrenSelectorChanges() {
        
        view.customStepperCellDidChangeValue(cell: ChildrenSelectorCell(), value: 1)
        XCTAssertTrue(presenter.childrenRequirementsChangedDidCall)
    }
}
