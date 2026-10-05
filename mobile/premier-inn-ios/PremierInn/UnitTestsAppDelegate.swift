//
//  UnitTestsAppDelegate.swift
//  PremierInn
//
//  Created by Marcello Mascia on 04/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork
import Firebase

class UnitTestsAppDelegate: UIResponder, UIApplicationDelegate {
    private lazy var appBootstrapper = AppBootstrapper(
        tasks: [
            EnvironmentBootstrap()
        ]
    )

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        let firebaseBootstrap = FirebaseBootstrap()
        firebaseBootstrap.run()

        appBootstrapper.start()

		return true
	}

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
