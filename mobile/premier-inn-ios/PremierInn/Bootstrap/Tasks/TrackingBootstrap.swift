//
//  TrackingBootstrap.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 08/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

struct TrackingBootstrap: BootstrapTask {
    private let appDelegate: AppDelegate
    private let appsFlyerManager: AppsFlyerManager
    private let dynatraceConfig: DynatraceConfigurable.Type
    private let contentsquareConfig: ContentsquareConfigurable.Type

    init(
        appDelegate: AppDelegate,
        appsFlyerManager: AppsFlyerManager = .sharedInstance,
        dynatraceConfig: DynatraceConfigurable.Type = DynatraceConfig.self,
        contentsquareConfig: ContentsquareConfigurable.Type = ContentsquareConfig.self
    ) {
        self.appDelegate = appDelegate
        self.appsFlyerManager = appsFlyerManager
        self.dynatraceConfig = dynatraceConfig
        self.contentsquareConfig = contentsquareConfig
    }

    func run() {
        appsFlyerManager.setUp(delegate: appDelegate)

        dynatraceConfig.setUp()
        contentsquareConfig.setUp()
    }
}
