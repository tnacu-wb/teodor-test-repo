//
//  EnvironmentBootstrapTests.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

final class EnvironmentBootstrapTests: XCTestCase {

    override func tearDown() {
        super.tearDown()
        MockRouterConfig.reset()
    }

    func testRunCallsConfigureImmediately() {
        // GIVEN
        let observer = MockObserverManager()
        let sut = EnvironmentBootstrap(
            observers: observer,
            routerConfig: MockRouterConfig.self
        )

        // WHEN
        sut.run()

        // THEN
        XCTAssertEqual(MockRouterConfig.callCount, 1)
    }

    func testRunRegistersMultiVariantObserver() {
        // GIVEN
        let observer = MockObserverManager()
        let sut = EnvironmentBootstrap(
            observers: observer,
            routerConfig: MockRouterConfig.self
        )

        // WHEN
        sut.run()

        // THEN
        XCTAssertTrue(observer.observedNames.contains(.multiVariantTestsDidLoad))
    }

    func testRunRegistersDevObserverWhenInDev() {
        // GIVEN
        let observer = MockObserverManager()
        let sut = EnvironmentBootstrap(
            observers: observer,
            routerConfig: MockRouterConfig.self
        )

        // WHEN
        sut.run()

        // THEN
        XCTAssertTrue(observer.observedNames.contains(.webserviceConfigurationDidChange))
    }

    func testObserverTriggerCallsConfigure() {
        // GIVEN
        let observer = MockObserverManager()
        let sut = EnvironmentBootstrap(
            observers: observer,
            routerConfig: MockRouterConfig.self
        )

        sut.run()

        // WHEN
        observer.handlers[.multiVariantTestsDidLoad]?(
            Notification(name: .multiVariantTestsDidLoad)
        )

        // THEN
        XCTAssertEqual(MockRouterConfig.callCount, 2)
    }

    func testMultipleObserverTriggersCallConfigureEachTime() {
        // GIVEN
        let observer = MockObserverManager()
        let sut = EnvironmentBootstrap(
            observers: observer,
            routerConfig: MockRouterConfig.self
        )

        sut.run()

        // WHEN
        observer.handlers[.multiVariantTestsDidLoad]?(
            Notification(name: .multiVariantTestsDidLoad)
        )
        observer.handlers[.multiVariantTestsDidLoad]?(
            Notification(name: .multiVariantTestsDidLoad)
        )

        // THEN
        XCTAssertEqual(MockRouterConfig.callCount, 3)
    }
}
