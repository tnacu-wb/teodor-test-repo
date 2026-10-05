//
//  AnalyticsBootstrapTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

@testable import PremierInn
import XCTest

final class AnalyticsBootstrapTests: XCTestCase {

    // MARK: - Properties

    private var analytics: MockAnalyticsManager!
    private var appsFlyer: MockAppsFlyerManager!
    private var adobe: MockAdobeCampaignManager!
    private var sut: AnalyticsBootstrap!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        analytics = MockAnalyticsManager()
        appsFlyer = MockAppsFlyerManager()
        adobe = MockAdobeCampaignManager()

        sut = AnalyticsBootstrap(
            analyticsManager: analytics,
            appsFlyerManager: appsFlyer,
            adobeCampaignManager: adobe
        )
    }

    override func tearDown() {
        analytics = nil
        appsFlyer = nil
        adobe = nil
        sut = nil

        super.tearDown()
    }

    // MARK: - Tests

    func testRunCallsAnalyticsSetup() {
        // GIVEN (handled in setUp)

        // WHEN
        sut.run()

        // THEN
        XCTAssertNotNil(analytics.setupCompletion)
    }

    func testRunCallsAdobeCampaignSetup() {
        // GIVEN

        // WHEN
        sut.run()

        // THEN
        XCTAssertEqual(adobe.setupCallCount, 1)
    }

    func testAnalyticsSetupCompletionTriggersAppsFlyerIntegration() {
        // GIVEN
        let visitorId = "visitor123"

        // WHEN
        sut.run()
        analytics.setupCompletion?(visitorId)

        // THEN
        XCTAssertEqual(appsFlyer.receivedCustomerId, visitorId)
    }

    func testAnalyticsSetupCompletionWithNilDoesNotIntegrateAppsFlyer() {
        // GIVEN

        // WHEN
        sut.run()
        analytics.setupCompletion?(nil)

        // THEN
        XCTAssertNil(appsFlyer.receivedCustomerId)
    }

    func testRunExecutesAllBehavioursTogether() {
        // GIVEN
        let visitorId = "abc123"

        // WHEN
        sut.run()
        analytics.setupCompletion?(visitorId)

        // THEN
        XCTAssertNotNil(analytics.setupCompletion)
        XCTAssertEqual(appsFlyer.receivedCustomerId, visitorId)
        XCTAssertEqual(adobe.setupCallCount, 1)
    }
}
