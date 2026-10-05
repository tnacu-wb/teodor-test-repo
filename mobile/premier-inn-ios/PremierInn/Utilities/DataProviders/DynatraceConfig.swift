//
//  DynatraceConfig.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 30/12/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import Dynatrace

protocol DynatraceConfigurable {
    static func setUp()
}

struct DynatraceConfig: DynatraceConfigurable {
    static func setUp() {
        let privacyConfig = Dynatrace.userPrivacyOptions()
        privacyConfig.dataCollectionLevel = .performance
        privacyConfig.crashReportingOptedIn = false
        Dynatrace.applyUserPrivacyOptions(privacyConfig) { (_) in
            // callback after privacy changed
            // Unsure how we want to handle this
        }
    }
}
