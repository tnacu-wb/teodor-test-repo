//
//  NavigationBarVisualToggles.swift
//  PremierInn
//
//  Created by Nick Jones on 26/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

extension UINavigationController {
    enum NavigationBarColours {
        /// Default `light`/`dark` theme
        case light
        /// Purple theme
        case premierInn


        /// The tint color to apply to the navigation items and bar button items.
        var tintColor: UIColor {
            switch self {
            case .light:
                return .ColourDL1
            case .premierInn:
                return .BaseWhite
            }
        }

        /// The tint color to apply to the navigation bar background.
        var barTintColor: UIColor {
            switch self {
            case .light:
                return .ColourLD5
            case .premierInn:
                return .BasePurple
            }
        }

        /// The navigation bar style that specifies its appearance.
        var barStyle: UIBarStyle {
            switch self {
            case .light:
                return .default
            case .premierInn:
                return .black
            }
        }

        /// Use this attribute to specify the color of the text during rendering
        var foregroundColor: UIColor {
            switch self {
            case .light:
                return .ColourDL1
            case .premierInn:
                return .BaseWhite
            }
        }

        var iconTintColor: UIColor {
            switch self {
            case .light:
                return .BasePurple
            case .premierInn:
                return .BaseWhite
            }
        }
    }

    func updateBarVisuals(withBottomBorder: Bool, theme: NavigationBarColours) {
        navigationBar.barStyle = theme.barStyle
        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = theme.barTintColor
        appearance.titleTextAttributes = [
            NSAttributedString.Key.font: UIFont.Heading4_Semibold(),
            NSAttributedString.Key.foregroundColor: theme.foregroundColor
        ]
        appearance.backButtonAppearance.normal.titleTextAttributes = [
            NSAttributedString.Key.font: UIFont.Action1(),
            NSAttributedString.Key.foregroundColor: theme.foregroundColor
        ]
        appearance.backButtonAppearance.highlighted.titleTextAttributes = [
            NSAttributedString.Key.font: UIFont.Action1(),
            NSAttributedString.Key.foregroundColor: theme.foregroundColor
        ]
        appearance.backButtonAppearance.disabled.titleTextAttributes = [
            NSAttributedString.Key.font: UIFont.Action1(),
            NSAttributedString.Key.foregroundColor: theme.foregroundColor
        ]
        appearance.buttonAppearance.normal.titleTextAttributes = [.foregroundColor: theme.foregroundColor]
        appearance.shadowColor = withBottomBorder ? .BaseBlack : .clear
        navigationBar.standardAppearance = appearance
        navigationBar.scrollEdgeAppearance = appearance
        navigationBar.tintColor = theme.tintColor
        setNeedsStatusBarAppearanceUpdate()
    }

    func setBottomBorder(toVisible visible: Bool) {
        navigationBar.setBackgroundImage(visible ? nil : UIImage(), for: .default)
        navigationBar.shadowImage = visible ? nil : UIImage()

        setNeedsStatusBarAppearanceUpdate()
    }
}
