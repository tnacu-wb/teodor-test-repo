//
//  FirebaseBootstrapTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
import FirebaseCore
@testable import PremierInn

final class FirebaseBootstrapTests: XCTestCase {

    override func tearDown() {
        super.tearDown()
        MockFirebaseConfigurator.reset()
    }

    func testRunCallsConfigureWithNilOptionsWhenPlistMissing() {
        // GIVEN: a bundle that returns no path
        let bundle = MockBundle()
        bundle.mockPath = nil

        let sut = FirebaseBootstrap(
            bundle: bundle,
            configurator: MockFirebaseConfigurator.self as any FirebaseAppConfigurable.Type
        )

        // WHEN: run is called
        sut.run()

        // THEN: configure is called with nil options
        XCTAssertEqual(MockFirebaseConfigurator.callCount, 1)
        XCTAssertNil(MockFirebaseConfigurator.receivedOptions)
    }

    func testRunCallsConfigureWithOptionsWhenPlistExists() {
        // GIVEN: a valid plist path
        let bundle = MockBundle()

        // Use a real plist file from test bundle or temp path
        let plistPath = Bundle(for: Self.self)
            .path(
                forResource: "GoogleService-Info",
                ofType: "plist"
            )

        bundle.mockPath = plistPath

        let sut = FirebaseBootstrap(
            bundle: bundle,
            configurator: MockFirebaseConfigurator.self as any FirebaseAppConfigurable.Type
        )

        // WHEN
        sut.run()

        // THEN: configure is called with non-nil options
        XCTAssertEqual(MockFirebaseConfigurator.callCount, 1)
        XCTAssertNotNil(MockFirebaseConfigurator.receivedOptions)
    }

    func testRunCallsConfigureExactlyOnce() {
        // GIVEN
        let bundle = MockBundle()
        bundle.mockPath = nil

        let sut = FirebaseBootstrap(
            bundle: bundle,
            configurator: MockFirebaseConfigurator.self as any FirebaseAppConfigurable.Type
        )

        // WHEN
        sut.run()

        // THEN
        XCTAssertEqual(MockFirebaseConfigurator.callCount, 1)
    }
}
