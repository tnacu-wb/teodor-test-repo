//
//  SupportedLanguage+Extensions.swift
//  PremierInn
//
//  Created by Santa Gurung on 17/05/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SimpleNetwork

public extension SupportedLanguage {
    var analyticsCode: String {
        switch self {
        case .english:
            return "UK"
        case .german:
            return "DE"
        }
    }
}
