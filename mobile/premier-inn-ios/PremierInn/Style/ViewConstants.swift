//
//  ViewConstants.swift
//  PremierInn
//
//  Created by Clint Mengolli on 07/11/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation

enum ViewConstants {
    enum ImageSize {
        static let xSmall: CGFloat = 8.0
        static let small: CGFloat  = 16.0
        static let medium: CGFloat = 24.0
        static let large: CGFloat  = 32.0
        static let xLarge: CGFloat = 48.0
    }

    enum BorderWidth {
        static let none: CGFloat   = 0.0
        static let small: CGFloat  = 1.0
        static let medium: CGFloat = 2.0
        static let large: CGFloat  = 4.0
    }

    enum CornerRadius {
        static let none: CGFloat   = 0
        static let xSmall: CGFloat = 4.0
        static let small: CGFloat  = 8.0
        static let medium: CGFloat = 16.0
        static let large: CGFloat  = 24.0
    }

    enum Spacing {
        static let none: CGFloat         = 0.0
        static let xSmall: CGFloat       = 4.0
        static let small: CGFloat        = 8.0
        static let medium: CGFloat       = 16.0
        static let mediumLarge: CGFloat  = 24.0
        static let large: CGFloat        = 32.0
        static let xLarge: CGFloat       = 48.0
        static let xxLarge: CGFloat      = 56.0
    }

    enum Opacity {
        static let transparent: Double = 0.0
        static let xLow: Double        = 0.25
        static let low: Double         = 0.5
        static let medium: Double      = 0.75
        static let full: Double        = 1.0
    }

    // Legacy spacing values for backward compatibility
    enum LegacyPadding {
        static let none: CGFloat   = 0.0
        static let small: CGFloat  = 10.0
        static let medium: CGFloat = 14.0
        static let large: CGFloat  = 30.0
    }
}
