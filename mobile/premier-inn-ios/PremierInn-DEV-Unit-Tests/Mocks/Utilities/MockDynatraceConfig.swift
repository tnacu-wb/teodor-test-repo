//
//  MockDynatraceConfig.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
@testable import PremierInn

enum MockDynatraceConfig: DynatraceConfigurable {

    static var callCount = 0

    static func setUp() {
        callCount += 1
    }

    static func reset() {
        callCount = 0
    }
}
