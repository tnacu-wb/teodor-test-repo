//
//  RateChangedAlertController.swift
//  PremierInn
//
//  Created by Freddie Parks on 26/04/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

protocol RateChangedAlertControllerOutput: AnyObject {
    func searchAgain(sender: UIViewController)
}

class RateChangedAlertController: UIAlertController {
    weak var controllerOutput: RateChangedAlertControllerOutput?

    var headingText: String?
    var messageText: String?
    var controllerSetup: ((_ controller: RateChangedViewController) -> Void)?
    var continueWithRate: (() -> Void)?
    var cancel: (() -> Void)?
    var rate: Rate?

    override func viewDidLoad() {
        super.viewDidLoad()

        style()
        setup()
    }

    private func style() {
        self.view.tintColor = .BasePurple
    }

    private func setup() {
        let contentViewController = RateChangedViewController(title: headingText, message: messageText)
        contentViewController.controllerOutput = self
        contentViewController.preferredContentSize.height = 600

        setValue(contentViewController, forKey: "contentViewController")

        controllerSetup?(contentViewController)
    }
}

extension RateChangedAlertController: RateChangedViewControllerOutput {
    func cancelAction(sender: UIViewController) {
        cancel?()
    }

    func continueWithRateButtonDidTap(sender: UIViewController) {
        continueWithRate?()
    }
}
