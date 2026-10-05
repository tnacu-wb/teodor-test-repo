//
//  CiolUpsellViewTests.swift
//  PremierInn
//
//  Created by Florin Velesca on 26.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn
@testable import SimpleNetwork

class MockCiolUpsellEventHandler: CiolUpsellViewEventHandler {
    var viewIsReadyCalled = false
    var continueButtonTappedCalled = false
    var reloadViewModelCalled = true
    var handleUpsellsRowCalled = false

    func viewIsReady() {
        viewIsReadyCalled = true
    }

    func handleContinueButtonTap() {
        continueButtonTappedCalled = true
    }
    
    func reloadViewModel() {
        reloadViewModelCalled = true
    }

    func handleUpsellsRowTapped(_ upsell: CiolUpsellItemViewModelProtocol) {
        handleUpsellsRowCalled = true
    }

    func getCellSetup(id: String, isSelected: Bool) -> PremierInn.CiolUpsellCellSetup? {
        return .init(isMultiRoom: false)
    }
    
    func trackPriceBreakdownTapAnalytics() { }

}

class CiolUpsellViewControllerTests: XCTestCase {
    struct MockCiolUpsellItemViewModel: CiolUpsellItemViewModelProtocol {
        var legendTitle: String = ""
        var imageURL: URL? = nil
        var title: String = ""
        var costSummary: String = ""
        var itemDescription: String? = ""
        var isFoodUpsell: Bool = false
        var isBooked: Bool = false
        var menuUrls: [RestaurantMenuItem] = [RestaurantMenuItem(name: "Test", description: nil, disclaimer: nil, path: "")]
        var allergensUrls: [AllergenInformation] = [("Test", "")]
        var subtitle: String = ""
        var hasChildren: Bool = false
        var isMultiRoom: Bool = false
        var subitems: [CiolUpsellSubitemViewModel] = []
        var room: UpsellRoom?
        var id: String = ""
        var enabled: Bool = false
        var bookingReference: String? = ""
        var selected: Bool = false
        var isPrebooked: Bool = false
        var isWifi: Bool = false
        var nights: Int = 0
    }
    
    var viewController: CiolUpsellViewController!
    var mockEventHandler: MockCiolUpsellEventHandler!

    override func setUp() {
        super.setUp()
        mockEventHandler = MockCiolUpsellEventHandler()
        viewController = CiolUpsellViewController()
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
    
    func testHandleUpsellsRowCalled() {
        viewController.eventHandler?.handleUpsellsRowTapped(MockCiolUpsellItemViewModel())
        XCTAssertTrue(mockEventHandler.handleUpsellsRowCalled, "handleUpsellsRowTapped() should be called")
    }
}
