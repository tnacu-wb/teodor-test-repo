//
//  AppearanceBootstrap.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 08/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

struct AppearanceBootstrap: BootstrapTask {
    func run() {
        // Navigation Bar
        UINavigationBar.appearance().titleTextAttributes = [
            .font: UIFont.NavTitle1(),
            .foregroundColor: UINavigationController.NavigationBarColours.light.foregroundColor
        ]
        UINavigationBar.appearance().barStyle = UINavigationController.NavigationBarColours.light.barStyle
        UINavigationBar.appearance().tintColor = UINavigationController.NavigationBarColours.light.tintColor
        UINavigationBar.appearance().isTranslucent = false

        let appearance = UINavigationBarAppearance()
        appearance.configureWithDefaultBackground()
        UINavigationBar.appearance().standardAppearance = appearance
        UINavigationBar.appearance().compactAppearance = appearance
        UINavigationBar.appearance().scrollEdgeAppearance = appearance

        UIBarButtonItem.appearance(whenContainedInInstancesOf: [UINavigationBar.self]).setTitleTextAttributes(
            [.font: UIFont.Action1()],
            for: .normal
        )
        UIBarButtonItem.appearance(whenContainedInInstancesOf: [UINavigationBar.self]).setTitleTextAttributes(
            [.font: UIFont.Action1()],
            for: .highlighted
        )
        UIBarButtonItem.appearance(whenContainedInInstancesOf: [UINavigationBar.self]).setTitleTextAttributes(
            [.font: UIFont.Action1()],
            for: .disabled
        )

        // Back button
        let backImg = #imageLiteral(resourceName: "back2").withAlignmentRectInsets(UIEdgeInsets(top: 0, left: 0, bottom: -3, right: 0))
        UINavigationBar.appearance().backIndicatorImage = backImg
        UINavigationBar.appearance().backIndicatorTransitionMaskImage = backImg

        // Tab Bar
        UITabBar.appearance().backgroundColor = .ColourLD5
        UITabBar.appearance().tintColor = .Tint1
        UITabBar.appearance().unselectedItemTintColor = .TintD2
        UITabBarItem.appearance().setTitleTextAttributes(
            [NSAttributedString.Key.font: UIFont.SubtextSmallStrong()],
            for: .normal
        )

        // Switch
        UISwitch.appearance().onTintColor = .Tint1
    }
}
