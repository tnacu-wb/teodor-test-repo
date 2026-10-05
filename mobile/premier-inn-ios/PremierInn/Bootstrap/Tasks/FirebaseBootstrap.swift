//
//  FirebaseBootstrap.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 08/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import FirebaseCore
import FirebaseInstallations

struct FirebaseBootstrap: BootstrapTask {
    private let bundle: Bundle
    private let configurator: FirebaseAppConfigurable.Type

    init(
        bundle: Bundle = .main,
        configurator: FirebaseAppConfigurable.Type = FirebaseAppConfig.self
    ) {
        self.bundle = bundle
        self.configurator = configurator
    }

    func run() {
        let path = bundle.path(
            forResource: FirebaseConstants.googleServiceInfo,
            ofType: "plist"
        )

        let options = path.flatMap { FirebaseOptions(contentsOfFile: $0) }

        configurator.configure(options: options)

        #if DEV
        logInstallationToken()
        #endif
    }

    private func logInstallationToken() {
        Installations.installations()
            .authTokenForcingRefresh(true) { result, error in
                if let error = error {
                    printDev("Error fetching token: \(error)")
                    return
                }

                printDev("Installation auth token: \(result?.authToken ?? "nil")")
            }
    }
}
