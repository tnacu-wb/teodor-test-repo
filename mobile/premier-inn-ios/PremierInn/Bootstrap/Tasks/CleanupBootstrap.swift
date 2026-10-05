//
//  CleanupBootstrap.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 08/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

struct CleanupBootstrap: BootstrapTask {
    private let settingsManager: SettingsManager
    private let userDefaults: UserDefaults

    init(
        settingsManager: SettingsManager = .sharedInstance,
        userDefaults: UserDefaults = .standard
    ) {
        self.settingsManager = settingsManager
        self.userDefaults = userDefaults
    }

    func run() {
        settingsManager.cleanupStoredData()

        userDefaults.set(
            false,
            forKey: Constants.dismissedCoronavirusMessaging
        )
    }
}
