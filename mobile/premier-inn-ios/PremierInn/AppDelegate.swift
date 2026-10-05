//
//  AppDelegate.swift
//  PremierInn
//
//  Created by Marcello Mascia on 12/05/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

// MARK: - AppDelegate

class AppDelegate: UIResponder, UIApplicationDelegate {
    // MARK: - Properties

    private lazy var appBootstrapper = AppBootstrapper(
        tasks: [
            CleanupBootstrap(),
            EnvironmentBootstrap(),
            CoreSettingsBootstrap(),
            AnalyticsBootstrap(),
            TrackingBootstrap(appDelegate: self),
            PushBootstrap(appDelegate: self),
            AppearanceBootstrap()
        ]
    )

    // MARK: - Lifecycle

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // Firebase must be configured before any Firebase-dependent singletons
        // are initialised. Since some legacy code touches Firebase during init,
        // we run this explicitly before the main bootstrapper.
        let firebaseBootstrap = FirebaseBootstrap()
        firebaseBootstrap.run()

        appBootstrapper.start()

        return true
    }

    func application(
        _ application: UIApplication,
        supportedInterfaceOrientationsFor window: UIWindow?
    ) -> UIInterfaceOrientationMask {
        guard UIDevice.current.userInterfaceIdiom != .pad else { return .all }

        return .portrait
    }

    // MARK: UISceneSession Lifecycle

    func application(
        _ application: UIApplication,
        configurationForConnecting connectingSceneSession: UISceneSession,
        options: UIScene.ConnectionOptions
    ) -> UISceneConfiguration {
        UISceneConfiguration(
            name: "Default Configuration",
            sessionRole: connectingSceneSession.role
        )
    }
}

// MARK: - Testing

extension AppDelegate {
    func resetApplicationForTesting() {
        // clear all user defaults
        UserDefaults.standard.removePersistentDomain(forName: Bundle.main.bundleIdentifier ?? "")
    }
}
