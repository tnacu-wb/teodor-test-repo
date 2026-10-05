//
//  GDPRViewModelTests.swift
//  PremierInn
//
//  Created by Santa Gurung on 23/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

final class GDPRViewModelTests: XCTestCase {

    let sut = GDPRViewModel()

    override func setUp() {
        UserDefaults.standard.set(false, forKey: Constants.hasAcceptedGDPRChanges)

        UserSessionManager.sharedInstance.piUserLoggedOut()
    }

    func testGDPRChangesAccepted() {
        sut.acceptGDPRChanges()

        let hasUserAccepted = UserDefaults.standard.bool(forKey: Constants.hasAcceptedGDPRChanges)
        XCTAssertTrue(hasUserAccepted)
    }

    func testAnalyticsVariables() {
        let mockAnalyticsManager = MockAnalyticsManager()
        sut.trackAnalytics(analyticsManager: mockAnalyticsManager)

        let stateName = mockAnalyticsManager.states.first!
        XCTAssertEqual(stateName, "iOS:PI:UK: Privacy Policy Acceptance Offer")

        let dataDictionary = mockAnalyticsManager.dictionaries.first!
        XCTAssertNotNil(dataDictionary)
        XCTAssertEqual(dataDictionary["analyticsData.all.screenType"], "iOS: GDPR")
    }
}
