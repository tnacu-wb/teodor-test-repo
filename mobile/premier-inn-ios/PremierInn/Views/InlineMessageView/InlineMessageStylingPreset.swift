//
//  InlineMessageStylingPreset.swift
//  PremierInn
//
//  Created by Clint Mengolli on 14/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

// MARK: - Styling

protocol InlineMessageStylingType {
    var textColor: UIColor { get }
    var textFont: UIFont { get }
    var borderColor: UIColor { get }
    var borderWidth: CGFloat { get }
    var cornerRadius: CGFloat { get }
    var backgroundColor: UIColor { get }
    var iconResourceName: String { get }
    var iconTint: UIColor? { get }
    var iconSize: CGFloat { get }
}

struct InlineMessageStyling: InlineMessageStylingType {
    let textColor: UIColor
    let textFont: UIFont
    let borderColor: UIColor
    let borderWidth: CGFloat
    let cornerRadius: CGFloat
    let backgroundColor: UIColor
    let iconResourceName: String
    let iconTint: UIColor?
    let iconSize: CGFloat
}

/// Future-proofing with the below enum in case there are info or success variants etc...
/// We could add the following cases with their own specific styling:
/// - case info
/// - case success
/// - case warning
/// - case custom(InlineMessageStylingType)

enum InlineMessageStylingPreset {
    case error

    var config: InlineMessageStylingType {
        switch self {
        case .error: return errorStyle
        }
    }

    private var errorStyle: InlineMessageStylingType {
        InlineMessageStyling(
            textColor: .BaseBlack,
            textFont: .BodySmall(),
            borderColor: .clear,
            borderWidth: 0,
            cornerRadius: 8,
            backgroundColor: .errorBackground,
            iconResourceName: "warningIcon",
            iconTint: nil,
            iconSize: 16
        )
    }
}
