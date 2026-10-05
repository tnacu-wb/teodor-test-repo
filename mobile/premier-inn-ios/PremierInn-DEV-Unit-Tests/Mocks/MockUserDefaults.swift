//
//  MockUserDefaults.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

final class MockUserDefaults: UserDefaults {

    init() {
        super.init(suiteName: "CleanupBootstrapTests")!
    }

    func reset() {
        removePersistentDomain(forName: "CleanupBootstrapTests")
    }
}
