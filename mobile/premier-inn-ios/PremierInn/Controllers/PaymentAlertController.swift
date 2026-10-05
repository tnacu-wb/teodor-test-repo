//
//  PaymentAlertController.swift
//  PremierInn
//
//  Created by Freddie Parks on 03/05/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class PaymentAlertController: UIAlertController {
    var headingText: String?
    var messageText: String?
    var controllerSetup: ((_ controller: TimeoutErrorViewController) -> Void)?

    override func viewDidLoad() {
        super.viewDidLoad()

        style()
        setup()
    }
    var completion: (() -> Void)?

    private func style() {
        self.view.tintColor = .BasePurple
    }

    private func setup() {
        let contentViewController = TimeoutErrorViewController(
            title: headingText,
            message: messageText,
            button: nil,
            accessibilityButtonLabel: nil
        )
        contentViewController.controllerOutput = self
        contentViewController.preferredContentSize.height = 427

        setValue(contentViewController, forKey: "contentViewController")

        controllerSetup?(contentViewController)
    }
}

extension PaymentAlertController: TimeoutErrorViewControllerOutput {
    func tryAgainButtonDidTap(sender: UIViewController) {
        completion?()
    }
}
