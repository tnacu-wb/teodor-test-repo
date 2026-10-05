//
//  Dictionary+Extensions.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 06/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

extension Dictionary {
    mutating func mergePreferNew(_ other: [Key: Value]) {
        self.merge(other) { _, new in new }
    }
}
