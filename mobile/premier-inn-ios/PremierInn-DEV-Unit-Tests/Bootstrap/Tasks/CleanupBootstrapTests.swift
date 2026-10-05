//
//  CleanupBootstrapTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

@testable import PremierInn
import XCTest

final class CleanupBootstrapTests: XCTestCase {

    // MARK: - Properties

    private var settingsManager: MockSettingsManager!
    private var userDefaults: MockUserDefaults!
    private var sut: CleanupBootstrap!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        settingsManager = MockSettingsManager()
        userDefaults = MockUserDefaults()
        userDefaults.reset()

        sut = CleanupBootstrap(
            settingsManager: settingsManager,
            userDefaults: userDefaults
        )
    }

    override func tearDown() {
        settingsManager = nil
        userDefaults = nil
        sut = nil

        super.tearDown()
    }

    // MARK: - Tests

    func testRunCallsCleanupStoredData() {
        // GIVEN

        // WHEN
        sut.run()

        // THEN
        XCTAssertEqual(settingsManager.cleanupCallCount, 1)
    }

    func testRunSetsDismissedCoronavirusMessagingToFalse() {
        // GIVEN

        // WHEN
        sut.run()

        // THEN
        let value = userDefaults.bool(
            forKey: Constants.dismissedCoronavirusMessaging
        )

        XCTAssertFalse(value)
    }

    func testRunWritesCorrectValueToUserDefaults() {
        // GIVEN

        // WHEN
        sut.run()

        // THEN
        XCTAssertNotNil(
            userDefaults.object(
                forKey: Constants.dismissedCoronavirusMessaging
            )
        )
    }

    func testRunExecutesAllSideEffectsTogether() {
        // GIVEN

        // WHEN
        sut.run()

        // THEN
        XCTAssertEqual(settingsManager.cleanupCallCount, 1)

        let value = userDefaults.bool(
            forKey: Constants.dismissedCoronavirusMessaging
        )

        XCTAssertFalse(value)
    }
}
