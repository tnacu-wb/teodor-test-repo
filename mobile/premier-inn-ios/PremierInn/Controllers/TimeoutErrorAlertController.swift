//
//  TimeoutErrorAlertController.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

protocol TimeoutErrorAlertControllerOutput: AnyObject {
    func tryAgainButtonDidTap(sender: UIViewController)
}

class TimeoutErrorAlertController: UIAlertController {
    weak var controllerOutput: TimeoutErrorAlertControllerOutput?

    var headingText: String?
    var messageText: String?
    var messageButton: String?
    var accessibilityButtonLabel: String?

    var controllerSetup: ((_ controller: TimeoutErrorViewController) -> Void)?

    override func viewDidLoad() {
        super.viewDidLoad()

        style()
        setup()
    }

    private func style() {
        self.view.tintColor = .BasePurple
    }

    private func setup() {
        let contentViewController = TimeoutErrorViewController(
            title: headingText,
            message: messageText,
            button: messageButton,
            accessibilityButtonLabel: accessibilityButtonLabel
        )
        contentViewController.controllerOutput = self
        contentViewController.preferredContentSize.height = 427

        setValue(contentViewController, forKey: "contentViewController")

        controllerSetup?(contentViewController)
    }
}

extension TimeoutErrorAlertController: TimeoutErrorViewControllerOutput {
    func tryAgainButtonDidTap(sender: UIViewController) {
        controllerOutput?.tryAgainButtonDidTap(sender: self)
    }
}
