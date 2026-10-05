//
//  SuggestionsViewControllerTests.swift
//  PremierInn UnitTests
//
//  Created by Filippo Minelle on 17/07/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

@testable import PremierInn

class SuggestionsViewControllerTests: XCTestCase {

    // MARK: - Properties

    private var viewController: SuggestionsViewController!
    private var analytics: MockAnalyticsManager!

    // MARK: - Lifecycle

    override func setUp() {

        viewController = SuggestionsViewController()

        analytics = MockAnalyticsManager()
        viewController.analytics = analytics
    }

    override func tearDown() {

        viewController = nil
        analytics = nil

        super.tearDown()
    }

    // MARK: - Tests

     func testView_whenMealPreferenceUpdated_invokesTrackAction() {

        let randomString = String().random(20)
        viewController.trackAnalytics(searchTerm: randomString)

        let action = analytics.actions.first
        let userInfo = analytics.userInfos.first

        XCTAssertEqual(action, "iOS: Invalid Search")
        XCTAssertEqual(userInfo?["searchLocation"], randomString)
        XCTAssertEqual(userInfo?["previousScreen"], "iOS: Hotel Details")
        XCTAssertEqual(userInfo?["loginStatus"], "unknown")
        XCTAssertEqual(userInfo?["visitNumber"], "???")
    }
}
