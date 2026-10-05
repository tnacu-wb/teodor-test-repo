//
//  MockBundle.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

final class MockBundle: Bundle, @unchecked Sendable {

    var mockPath: String?

    override func path(forResource name: String?, ofType ext: String?) -> String? {
        return mockPath
    }
}
