//
//  CoreSettingsBootstrapTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

final class CoreSettingsBootstrapTests: XCTestCase {

    // MARK: - Properties

    private var bookingDetails: MockBookingDetails!
    private var settingsManager: MockSettingsManager!
    private var googleAPIProvider: MockGoogleAPIDataProvider!
    private var sut: CoreSettingsBootstrap!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        bookingDetails = MockBookingDetails()
        settingsManager = MockSettingsManager()
        googleAPIProvider = MockGoogleAPIDataProvider()

        sut = CoreSettingsBootstrap(
            bookingDetails: bookingDetails,
            settingsManager: settingsManager,
            googleAPIDataProvider: googleAPIProvider
        )
    }

    override func tearDown() {
        bookingDetails = nil
        settingsManager = nil
        googleAPIProvider = nil
        sut = nil

        super.tearDown()
    }

    // MARK: - Tests

    func testRunSetsEmployeeRatesFromSettingsManagerWhenEnabled() {
        // GIVEN
        settingsManager.enableEmployeeRates = true

        // WHEN
        sut.run()

        // THEN
        XCTAssertEqual(bookingDetails.employeeRatesEnabledValue, true)
    }

    func testRunSetsEmployeeRatesFromSettingsManagerWhenDisabled() {
        // GIVEN
        settingsManager.enableEmployeeRates = false

        // WHEN
        sut.run()

        // THEN
        XCTAssertEqual(bookingDetails.employeeRatesEnabledValue, false)
    }

    func testRunCallsGoogleAPIDataProviderSetup() {
        // GIVEN (handled in setUp)

        // WHEN
        sut.run()

        // THEN
        XCTAssertEqual(googleAPIProvider.setupCallCount, 1)
    }

    func testRunExecutesAllSideEffectsTogether() {
        // GIVEN
        settingsManager.enableEmployeeRates = true

        // WHEN
        sut.run()

        // THEN
        XCTAssertEqual(bookingDetails.employeeRatesEnabledValue, true)
        XCTAssertEqual(googleAPIProvider.setupCallCount, 1)
    }
}
