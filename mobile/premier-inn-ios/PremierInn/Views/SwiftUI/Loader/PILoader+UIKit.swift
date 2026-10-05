//
//  PILoader+UIKit.swift
//  PremierInn
//
//  Created by Badea, Bogdan (Cognizant) on 26.05.2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI
import UIKit

private final class PILoaderHostingController: UIHostingController<PILoader> {
    // MARK: - Init

    init() {
        super.init(rootView: PILoader())
        view.backgroundColor = .clear
    }

    dynamic required init?(coder _: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

// MARK: - Helper methods

extension UIViewController {
    func showLoader(_ shouldShow: Bool = true) {
        if !shouldShow {
            hideLoader()
            return
        }

        guard children.contains(where: { $0 is PILoaderHostingController }) == false else { return }

        let hostingController = PILoaderHostingController()

        addChild(hostingController)

        hostingController.view.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(hostingController.view)

        NSLayoutConstraint.activate([
            hostingController.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            hostingController.view.topAnchor.constraint(equalTo: view.topAnchor),
            hostingController.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            hostingController.view.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])

        hostingController.didMove(toParent: self)
    }

    func hideLoader() {
        guard let loaderController = children.first(where: { $0 is PILoaderHostingController }) else { return }

        loaderController.willMove(toParent: nil)
        loaderController.view.removeFromSuperview()
        loaderController.removeFromParent()
    }
}
