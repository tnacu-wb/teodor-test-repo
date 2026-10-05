//
//  DefaultAnalyticsDataTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 16/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Testing
import SimpleNetwork
@testable import PremierInn

struct DefaultAnalyticsDataTests {

    private var sut: DefaultAnalyticsData!

    private struct AnalyticsData: DefaultAnalyticsData {
        var userSession: UserSessionManagerProtocol { MockUserSessionManager() }
        var environment: String { "XCTestSuite" }
        var loggedIn: LoggedInAnalytic { .notLoggedIn }
        var timeZone: String { "SOME_TIMEZONE" }
        var language: String { "SOME_LANGUAGE" }
        var screenType: String { "PI_DEV_TEST" }
        var userID: String { "SOME_USER_ID" }
        var time: String { "DUMMY_TIME" }
    }

    @Test("mergingDefaultValues when there is the `campaignAttribution` property is `nil`")
    mutating func testMergingDefaultValues_whenCampaignAttributionInAnalyticsManagerIsNil() {
        // GIVEN sut and data dictionary to test
        sut = AnalyticsData()
        let mockAnalyticsManager = MockAnalyticsManager()
        let dataDict = [PIAnalytics.Keys.errorMessage: "error"]

        // WHEN `mergingDefaultValues` is called
        let result = sut.mergingDefaultValues(with: dataDict, analytics: mockAnalyticsManager)

        // THEN
        #expect(result.keys.contains(PIAnalytics.Keys.errorMessage), "Should contain the dataDict keys")
        #expect(result.count == 8, "Should be 8 keys along with the dataDict key")
    }

    @Test("mergingDefaultValues when there is the `campaignAttribution` property is set")
    mutating func testMergingDefaultValues_whenCampaignAttributionInAnalyticsManagerIsSet() {
        // GIVEN sut and data dictionary to test
        sut = AnalyticsData()
        let mockAnalyticsManager = MockAnalyticsManager()

        mockAnalyticsManager.campaignAttribution = .init(
            fullURLString: "SOME_STRING",
            referrerURLString: "SOME_STRING",
            cid: "SOME_STRING",
            mckv: "SOME_STRING",
            etRid: "SOME_STRING"
        )

        let dataDict = [PIAnalytics.Keys.errorMessage: "error"]

        // WHEN `mergingDefaultValues` is called
        let result = sut.mergingDefaultValues(with: dataDict, analytics: mockAnalyticsManager)

        // THEN
        #expect(result.keys.contains(PIAnalytics.Keys.errorMessage), "Should contain the dataDict keys")
        #expect(result.count == 13, "Should be 13 keys along with the dataDict key")
    }
}
