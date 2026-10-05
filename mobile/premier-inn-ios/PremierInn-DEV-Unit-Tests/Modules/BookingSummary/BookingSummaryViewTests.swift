//
//  BookingSummaryViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 04/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockPresenter: BookingSummaryPresenterProtocol {

    var viewIsReadyDidCall = false

    func viewIsReady() {

        viewIsReadyDidCall = true
    }
}

class BookingSummaryViewTests: XCTestCase {

    private var viewController: BookingSummaryViewController!
    private var presenter: MockPresenter!
    private var analytics: MockAnalyticsManager!
    private var screenName: String!

    // MARK: - Lifecycle

    override func setUp() {

        presenter = MockPresenter()
        screenName = String().random()

        viewController = BookingSummaryViewController(screenName: screenName)
        viewController.presenter = presenter

        analytics = MockAnalyticsManager()
        viewController.analytics = analytics
    }

    override func tearDown() {

        presenter = nil
        viewController = nil
        analytics = nil

        super.tearDown()
    }

    // MARK: - Tests

    func testView_whenViewAppears_invokesTrackState() {

        viewController.beginAppearanceTransition(true, animated: false)
        viewController.endAppearanceTransition()

        let state = analytics.states.first

        XCTAssertEqual(state, screenName)
    }

    func testViewDidLoad() {

        viewController?.viewDidLoad()
        XCTAssertTrue(presenter.viewIsReadyDidCall)
    }

}
