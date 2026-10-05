//
//  PushBootstrapTests.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

@testable import PremierInn
import XCTest

final class PushBootstrapTests: XCTestCase {

    private var appDelegate: MockAppDelegate!
    private var messaging: MockMessaging!
    private var sut: PushBootstrap!

    override func setUp() {
        super.setUp()

        appDelegate = MockAppDelegate()
        messaging = MockMessaging()

        sut = PushBootstrap(
            appDelegate: appDelegate,
            firebaseMessaging: messaging
        )
    }

    override func tearDown() {
        appDelegate = nil
        messaging = nil
        sut = nil
        super.tearDown()
    }

    func testRunSetsMessagingDelegate() {
        // WHEN
        sut.run()

        // THEN
        XCTAssertTrue(messaging.delegate === appDelegate)
    }

    func testRunSetsMessagingDelegateNotNil() {
        // WHEN
        sut.run()

        // THEN
        XCTAssertNotNil(messaging.delegate)
    }
}
