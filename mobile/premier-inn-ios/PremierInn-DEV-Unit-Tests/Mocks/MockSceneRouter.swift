//
//  MockSceneRouter.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit
@testable import PremierInn

final class MockSceneRouter: SceneRouting {

    private(set) var didBecomeActiveCalled = false
    private(set) var willResignActiveCalled = false
    private(set) var willEnterForegroundCalled = false
    private(set) var didEnterBackgroundCalled = false

    private(set) var handleShortcutCalled = false
    private(set) var handleURLCalled = false
    private(set) var handleUserActivityCalled = false

    func start(with options: UIScene.ConnectionOptions) { }

    func sceneDidBecomeActive() {
        didBecomeActiveCalled = true
    }

    func sceneWillResignActive() {
        willResignActiveCalled = true
    }

    func sceneWillEnterForeground() {
        willEnterForegroundCalled = true
    }

    func sceneDidEnterBackground() {
        didEnterBackgroundCalled = true
    }

    func handle(shortcutItem: UIApplicationShortcutItem) -> Bool {
        handleShortcutCalled = true
        return handleShortcutCalled
    }

    func handle(_ url: URL) {
        handleURLCalled = true
    }

    func handle(_ userActivity: NSUserActivity) {
        handleUserActivityCalled = true
    }
}
