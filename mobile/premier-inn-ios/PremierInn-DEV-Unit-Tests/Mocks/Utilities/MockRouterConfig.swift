//
//  MockRouterConfig.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
@testable import PremierInn

final class MockRouterConfig: RouterConfigurable {

    static var callCount = 0

    static func configure() {
        callCount += 1
    }

    static func reset() {
        callCount = 0
    }

    static var title: String {
        ""
    }
}
