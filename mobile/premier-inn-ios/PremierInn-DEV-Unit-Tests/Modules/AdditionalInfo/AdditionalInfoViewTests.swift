//
//  AdditionalInfoViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 13/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest

import SimpleNetwork
@testable import PremierInn

private class MockEventHandler: AdditionalInfoViewEventHandler {

    var viewIsReadyDidCall = false
    var closeDidCall = false

    func viewIsReady() {

        viewIsReadyDidCall = true
    }

    func closeButtonDidTap() {

        closeDidCall = true
    }
}

class AdditionalInfoViewTests: XCTestCase {

    struct ViewModel: AdditionalInfoViewModel {
        var facilityDescriptions: [String]?
        var facilityTitle: String?
        var roomFeatureDescriptions: [String]?
        var roomFeatureTitle: String?
        var parkingDetails: String?
        let infoType: AdditionalInfoType
        let hotelName: String
        let hotelNotes: [String]?
        let hotelDescription: String?
        let hotelDirections: String?
    }

    fileprivate var mockEventHandler: MockEventHandler?

    private var viewController: AdditionalInfoViewController?
    private var analytics: MockAnalyticsManager!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        mockEventHandler = MockEventHandler()

        viewController = AdditionalInfoViewController()
        viewController?.table = UITableView()
        viewController?.eventHandler = mockEventHandler

        analytics = MockAnalyticsManager()
        viewController?.analytics = analytics
    }

    override func tearDown() {

        viewController = nil
        analytics = nil

        super.tearDown()
    }

    // MARK: - Tests

    func testView_whenViewAppears_invokesTrackState() {
        UserSessionManager.sharedInstance.piUserLoggedOut()

        viewController?.beginAppearanceTransition(true, animated: false)
        viewController?.endAppearanceTransition()

        let state = analytics.states.first
        
        XCTAssertEqual(state, "iOS:PI:UK: Hotel Details - Important information")
    }

    func testsViewIsReady() {

        viewController?.viewDidLoad()

        XCTAssert(mockEventHandler?.viewIsReadyDidCall == true)
    }

    func testUpdateHotelInfo() {
        viewController?.loadView()
        let viewModel = ViewModel(
            infoType: .hotelNotes,
            hotelName: "",
            hotelNotes: ["something", "something else"],
            hotelDescription: nil,
            hotelDirections: nil
        )

        viewController?.update(with: viewModel)

        XCTAssert(viewController?.table.numberOfSections == 1)
        XCTAssert(viewController?.table.numberOfRows(inSection: 0) == 2)
    }

    func testUpdateHotelLocation() {
        viewController?.loadView()
        let viewModel = ViewModel(
            infoType: .hotelLocation,
            hotelName: "ABC",
            hotelNotes: nil,
            hotelDescription: "Quality",
            hotelDirections: "turn left"
        )

        viewController?.update(with: viewModel)

        XCTAssert(viewController?.table.numberOfSections == 1)
        XCTAssert(viewController?.table.numberOfRows(inSection: 0) == 3)

        let titleIndexPath = IndexPath(row: 0, section: 0)
        XCTAssert(viewController?.table.cellForRow(at: titleIndexPath) is HotelTitleCell)
    }

    func testClose() {

        viewController?.closeButtonDidTap()

        XCTAssert(mockEventHandler?.closeDidCall == true)
    }
}
