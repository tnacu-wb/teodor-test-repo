//
//  TailProperties.swift
//  PremierInn
//
//  Created by Clint Mengolli on 13/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

enum TailHost: Equatable {
    case topBorder(alignment: TailAlignment)
    case bottomBorder(alignment: TailAlignment)
    case none

    var alignment: TailAlignment {
        switch self {
        case .topBorder(let alignment):
            return alignment
        case .bottomBorder(let alignment):
            return alignment
        case .none:
            return TailAlignment.none
        }
    }

    var isTop: Bool {
        if case .topBorder = self {
            return true
        }
        return false
    }

    var isBottom: Bool {
        if case .bottomBorder = self {
            return true
        }
        return false
    }
}

enum TailAlignment: Equatable {
    case centered
    case leading(offsetBy: CGFloat = 16)
    case trailing(offsetBy: CGFloat = 16)
    case custom(offsetBy: CGFloat)
    case none
}
