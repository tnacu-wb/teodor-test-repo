//
//  DebugRouter.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 11/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

final class DebugButtonRouter {
    func update(in window: UIWindow?) {
        guard let window else { return }

        // Remove existing
        window.viewWithTag(DebugButtonView.identifier)?.removeFromSuperview()

        guard SettingsManager.sharedInstance.shouldShowDebugButtonOverlay else {
            return
        }

        let button = DebugButtonView(
            frame: CGRect(
                x: 10,
                y: window.frame.height - 54,
                width: 44,
                height: 44
            )
        )

        button.update(title: RouterConfig.title)

        button.addTarget(self, action: #selector(openDebug(_:)), for: .touchUpInside)

        window.addSubview(button)
    }

    @objc private func openDebug(_ sender: UIButton) {
        guard let window = sender.window,
              let root = window.rootViewController as? ProcessingViewController,
              let tab = root.presentedViewController as? UITabBarController else {
            return
        }

        let vc = DebugViewController()
        let nav = UINavigationController(rootViewController: vc)

        tab.present(nav, animated: true)
    }
}
