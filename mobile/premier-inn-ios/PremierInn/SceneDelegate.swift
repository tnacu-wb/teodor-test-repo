//
//  SceneDelegate.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 08/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

final class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    var window: UIWindow?
    var router: SceneRouting!

    func scene(
        _ scene: UIScene,
        willConnectTo session: UISceneSession,
        options connectionOptions: UIScene.ConnectionOptions
    ) {
        guard let window else { return }

        router = router ?? SceneRouter(window: window)

        router.start(with: connectionOptions)
    }

    func sceneDidBecomeActive(_ scene: UIScene) {
        router.sceneDidBecomeActive()
    }

    func sceneWillResignActive(_ scene: UIScene) {
        router.sceneWillResignActive()
    }

    func sceneWillEnterForeground(_ scene: UIScene) {
        router.sceneWillEnterForeground()
    }

    func sceneDidEnterBackground(_ scene: UIScene) {
        router.sceneDidEnterBackground()
    }

    func windowScene(
        _ windowScene: UIWindowScene,
        performActionFor shortcutItem: UIApplicationShortcutItem,
        completionHandler: @escaping (Bool) -> Void
    ) {
        router.handle(shortcutItem: shortcutItem)
    }

    func scene(
        _ scene: UIScene,
        openURLContexts URLContexts: Set<UIOpenURLContext>
    ) {
        guard let url = URLContexts.first?.url else {
            return
        }

        router.handle(url)
    }

    func scene(
        _ scene: UIScene,
        continue userActivity: NSUserActivity
    ) {
        router.handle(userActivity)
    }
}
