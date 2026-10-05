//
//  SceneDelegateTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

@testable import PremierInn
import XCTest

final class SceneDelegateTests: XCTestCase {

    // MARK: - Properties

    private var sut: SceneDelegate!
    private var router: MockSceneRouter!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        sut = SceneDelegate()
        router = MockSceneRouter()

        sut.router = router
    }

    override func tearDown() {
        sut = nil
        router = nil
        super.tearDown()
    }

    // MARK: - Tests

    func testSceneDidBecomeActiveCallsRouter() {
        // GIVEN

        // WHEN
        sut.sceneDidBecomeActive(unsafeScene)

        // THEN
        XCTAssertTrue(router.didBecomeActiveCalled)
    }

    func testSceneWillResignActiveCallsRouter() {
        // GIVEN

        // WHEN
        sut.sceneWillResignActive(unsafeScene)

        // THEN
        XCTAssertTrue(router.willResignActiveCalled)
    }

    func testSceneWillEnterForegroundCallsRouter() {
        // GIVEN

        // WHEN
        sut.sceneWillEnterForeground(unsafeScene)

        // THEN
        XCTAssertTrue(router.willEnterForegroundCalled)
    }

    func testSceneDidEnterBackgroundCallsRouter() {
        // GIVEN

        // WHEN
        sut.sceneDidEnterBackground(unsafeScene)

        // THEN
        XCTAssertTrue(router.didEnterBackgroundCalled)
    }

    func testShortcutDelegatesToRouter() {
        // GIVEN
        let shortcut = UIApplicationShortcutItem(
            type: "test.shortcut",
            localizedTitle: "Test"
        )

        // WHEN
        sut.windowScene(unsafeWindowScene, performActionFor: shortcut) { _ in }

        // THEN
        XCTAssertTrue(router.handleShortcutCalled)
    }

    func testOpenURLContextsWithEmptySetDoesNotCallRouter() {
        // GIVEN
        let contexts: Set<UIOpenURLContext> = []

        // WHEN
        sut.scene(unsafeScene, openURLContexts: contexts)

        // THEN
        XCTAssertFalse(router.handleURLCalled)
    }

    func testContinueUserActivityDelegatesToRouter() {
        // GIVEN
        let activity = NSUserActivity(activityType: "test.activity")

        // WHEN
        sut.scene(unsafeScene, continue: activity)

        // THEN
        XCTAssertTrue(router.handleUserActivityCalled)
    }
}

private extension SceneDelegateTests {
    
    var unsafeScene: UIScene {
        unsafeBitCast(0, to: UIScene.self)
    }

    var unsafeWindowScene: UIWindowScene {
        unsafeBitCast(0, to: UIWindowScene.self)
    }
}
