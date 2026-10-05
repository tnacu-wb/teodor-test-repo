//
//  TrackingBootstrapTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

@testable import PremierInn
import XCTest

final class TrackingBootstrapTests: XCTestCase {

    // MARK: - Properties

    private var appDelegate: MockAppDelegate!
    private var appsFlyerManager: MockAppsFlyerManager!
    private var sut: TrackingBootstrap!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        appDelegate = MockAppDelegate()
        appsFlyerManager = MockAppsFlyerManager()

        sut = TrackingBootstrap(
            appDelegate: appDelegate,
            appsFlyerManager: appsFlyerManager,
            dynatraceConfig: MockDynatraceConfig.self,
            contentsquareConfig: MockContentsquareConfig.self
        )
    }

    override func tearDown() {
        appDelegate = nil
        appsFlyerManager = nil
        sut = nil

        MockDynatraceConfig.reset()
        MockContentsquareConfig.reset()

        super.tearDown()
    }

    // MARK: - Tests

    func testRunCallsAppsFlyerSetupWithAppDelegate() {
        // GIVEN (setup)

        // WHEN
        sut.run()

        // THEN
        XCTAssertTrue(appsFlyerManager.receivedDelegate === appDelegate)
    }

    func testRunCallsDynatraceSetup() {
        // GIVEN

        // WHEN
        sut.run()

        // THEN
        XCTAssertEqual(MockDynatraceConfig.callCount, 1)
    }

    func testRunCallsContentsquareSetup() {
        // GIVEN

        // WHEN
        sut.run()

        // THEN
        XCTAssertEqual(MockContentsquareConfig.callCount, 1)
    }

    func testRunExecutesAllSideEffectsTogether() {
        // GIVEN

        // WHEN
        sut.run()

        // THEN
        XCTAssertTrue(appsFlyerManager.receivedDelegate === appDelegate)
        XCTAssertEqual(MockDynatraceConfig.callCount, 1)
        XCTAssertEqual(MockContentsquareConfig.callCount, 1)
    }
}
