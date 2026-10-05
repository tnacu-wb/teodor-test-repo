//
//  EnvironmentBootstrap.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 08/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

struct EnvironmentBootstrap: BootstrapTask {
    private let observerManager: ObserverManageable
    private let routerConfig: RouterConfigurable.Type

    init(
        observers: ObserverManageable = NotificationObserverManager(),
        routerConfig: RouterConfigurable.Type = RouterConfig.self
    ) {
        self.observerManager = observers
        self.routerConfig = routerConfig
    }

    func run() {
        configure()

        observerManager.observe(name: .multiVariantTestsDidLoad) { _ in
            routerConfig.configure()
        }

        #if DEV
        observerManager.observe(name: .webserviceConfigurationDidChange) { _ in
            routerConfig.configure()
        }
        #endif
    }

    private func configure() {
        routerConfig.configure()
    }
}
