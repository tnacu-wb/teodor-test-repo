//
//  RoomsUpsellViewTests.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 15.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

class MockRoomsUpsellEventHandler: RoomsUpsellModuleEventHandler {

    var editedUpsell: (any PremierInn.CiolUpsellItemViewModelProtocol)?
    var viewIsReadyCalled = false
    var continueButtonTappedCalled = false
    var didCallShowUpsellDetails = false
    var didCallUpdateOutput = false

    func viewIsReady() {
        viewIsReadyCalled = true
    }

    func handleContinueButtonTap() {
        continueButtonTappedCalled = true
    }

    func showUpsellDetails(room: UpsellRoom) {
        didCallShowUpsellDetails = true
    }

    func updateOutput() {
        didCallUpdateOutput = true
    }

    func getRoomCellConfig(roomID: String) -> PremierInn.CiolUpsellCellSetup? {
        return .init(isMultiRoom: false)
    }
}

class RoomsUpsellViewTests: XCTestCase {

    var viewController: RoomsUpsellViewController!
    var mockEventHandler: MockRoomsUpsellEventHandler!

    override func setUp() {
        super.setUp()
        mockEventHandler = MockRoomsUpsellEventHandler()
        viewController = RoomsUpsellViewController()
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
}
