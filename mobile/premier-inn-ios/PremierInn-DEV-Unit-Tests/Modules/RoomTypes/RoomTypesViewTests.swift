//
//  RoomTypesViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Georgios Aikaterinakis on 11/06/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockPresenter: RoomTypesViewEventHandler {
    
    var closeButtonDidTapDidCall = false
    var viewNeedsSetupDidCall = false
    var viewIsReadyDidCall = false
    var segmentChangedDidCall = false
    
    func closeButtonDidTap() {
        
        closeButtonDidTapDidCall = true
    }
    
    func viewNeedsSetup() {
        
        viewNeedsSetupDidCall = true
    }
    
    func viewIsReady() {
        
        viewIsReadyDidCall = true
    }
    
    func segmentChanged(selectedIndex: Int) {
        
        segmentChangedDidCall = true
    }
}

class RoomTypesViewTests: XCTestCase {
    
    private var view: RoomTypesView!
    private var eventHandler: MockPresenter!
    
    override func setUp() {
        
        eventHandler = MockPresenter()
        
        view = RoomTypesView()
        view.eventHandler = eventHandler
    }
    
    override func tearDown() {
        
        eventHandler = nil
        view = nil
        
        super.tearDown()
    }
    
    func testViewDidLoad() {

        view.loadView()
        view.viewDidLoad()
        XCTAssertTrue(eventHandler.viewNeedsSetupDidCall)
        XCTAssertTrue(eventHandler.viewIsReadyDidCall)
    }
    
    func testCloseButtonDidTap() {
        
        view.closeButtonDidTap()
        XCTAssertTrue(eventHandler.closeButtonDidTapDidCall)
    }
    
    func testSegmentChanged() {
        
        // needs content to test
        //        view.hotelInformationSegmentsCellSegmentDidChange(cell: HotelInformationSegmentsCell())
        //        XCTAssertTrue(eventHandler.segmentChangedDidCall)
    }
    
}
