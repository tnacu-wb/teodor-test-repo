//
//  PINavigationController.swift
//  PremierInn
//
//  Created by Marcello Mascia on 05/12/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class MyBookingsNavigationController: UINavigationController {
}

class PINavigationController: UINavigationController {
    override func viewDidLoad() {
        super.viewDidLoad()

        #if DEV
            NotificationCenter.default.addObserver(
                self,
                selector: #selector(routerDidChange),
                name: .webserviceConfigurationDidChange,
                object: nil
            )
        #endif

        delegate = self
    }

    @objc private func routerDidChange() {
        popToRootViewController(animated: false)
    }
}

extension PINavigationController: UINavigationControllerDelegate {
    func navigationController(
        _ navigationController: UINavigationController,
        animationControllerFor operation: UINavigationController.Operation,
        from fromVC: UIViewController,
        to toVC: UIViewController
    ) -> UIViewControllerAnimatedTransitioning? {
        nil
    }

    func navigationController(
        _ navigationController: UINavigationController,
        interactionControllerFor animationController: UIViewControllerAnimatedTransitioning
    ) -> UIViewControllerInteractiveTransitioning? {
        nil
    }
}
