//
//  ShortcutIdentifier.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 11/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

enum ShortcutIdentifier: String {
    case search
    case hotelsNearMe
    case hotelsNearLocation

    init?(fullType: String) {
        guard let last = fullType.components(separatedBy: ".").last else {
            return nil
        }

        self.init(rawValue: last)
    }

    private var bundleIdentifier: String {
        #if DEV
        return "com.whitbread.pi"
        #else
        return "com.whitbread.PremierInn"
        #endif
    }

    var type: String {
        bundleIdentifier + ".\(self.rawValue)"
    }
}
