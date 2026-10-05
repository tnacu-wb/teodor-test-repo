//
//  View+Extensions.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 29/09/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import SwiftUI

extension View {
    var hostingController: UIViewController? {
        UIApplication.shared
            .connectedScenes
            .compactMap { ($0 as? UIWindowScene)?.keyWindow?.rootViewController }
            .first?
            .topMostViewController()
    }

    func embedInHostingController() -> UIViewController {
        UIHostingController(rootView: self)
    }
}

// helper to climb presentation stack
extension UIViewController {
    func topMostViewController() -> UIViewController {
        if let presented = presentedViewController {
            return presented.topMostViewController()
        }
        if let nav = self as? UINavigationController {
            return nav.visibleViewController?.topMostViewController() ?? nav
        }
        if let tab = self as? UITabBarController {
            return tab.selectedViewController?.topMostViewController() ?? tab
        }
        return self
    }
}

extension UINavigationController {
    func setSwipeGesturePopsViewController(_ isEnabled: Bool) {
        self.interactivePopGestureRecognizer?.isEnabled = isEnabled
    }
}
