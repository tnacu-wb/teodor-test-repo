//
//  CiolInformationViewControllerTests.swift
//  PremierInnTests
//
//  Created by Muresan, Andreea (Cognizant) on 11.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

class CiolInformationViewControllerTests: XCTestCase {

    var viewController: CiolInformationViewController!
    var mockEventHandler: MockCiolInformationViewEventHandler!
    
    override func setUp() {
        super.setUp()
        mockEventHandler = MockCiolInformationViewEventHandler()
    }
    
    override func tearDown() {
        viewController = nil
        mockEventHandler = nil
        super.tearDown()
    }

    func makeVC(screenName: String?) -> CiolInformationViewController {
        let info = CiolBottomSheetAnalyticsInfo(
            screenNameForViewUnderneath: screenName
        )
        let vc = CiolInformationViewController(
            ciolBottomSheetAnalyticsInfo: info
        )
        vc.eventHandler = mockEventHandler
        return vc
    }

    func testViewDidLoad_CallsPresenterViewIsReady() {
        viewController = makeVC(screenName: nil)
        // Act
        viewController.loadViewIfNeeded()
        
        // Assert
        XCTAssertTrue(mockEventHandler.isViewIsReadyCalled, "viewDidLoad should call viewIsReady on the presenter")
    }

    func testActionButtonTapWhenViewControllerIsTrackable() {
        viewController = makeVC(screenName: "Screen_That_Needs_Tracking")
        viewController.actionButtonDidTap(UIButton())
        XCTAssertTrue(mockEventHandler.isAnalyticsTracked)
    }

    func testActionButtonTapWhenViewControllerIsNotTrackable() {
        viewController = makeVC(screenName: nil)
        viewController.actionButtonDidTap(UIButton())
        XCTAssertFalse(mockEventHandler.isAnalyticsTracked)
    }
}

// Mock Presenter
class MockCiolInformationViewEventHandler: CiolInformationViewEventHandler {
    var customAnalyticsParameters: PIDictionary?
    var isViewIsReadyCalled = false
    var isAnalyticsTracked = false

    func viewIsReady() {
        isViewIsReadyCalled = true
    }

    func close() {

    }
    
    func showQrCode() { }

    func logActionAnalytics() {
        isAnalyticsTracked = true
    }
}
