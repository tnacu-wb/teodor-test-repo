//
//  RateChangedViewController.swift
//  PremierInn
//
//  Created by Freddie Parks on 26/04/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

protocol RateChangedViewControllerOutput: AnyObject {
    func cancelAction(sender: UIViewController)
    func continueWithRateButtonDidTap(sender: UIViewController)
}

class RateChangedViewController: UIViewController {
    @IBOutlet var ratePrice: UILabel! {
        didSet {
            ratePrice.font = .Heading1_Semibold()
        }
    }
    @IBOutlet var searchAgainButton: FadeOnHighlightButton! {
        didSet {
            searchAgainButton.titleLabel?.font = .Button1()
        }
    }

    weak var controllerOutput: RateChangedViewControllerOutput?

    @IBOutlet weak var heading: UILabel! {
        didSet {
            heading.font = .Heading1_Semibold()
        }
    }
    @IBOutlet weak var message: UILabel! {
        didSet {
            message.font = .Body()
        }
    }
    @IBOutlet weak var ctaButton: FadeOnHighlightButton! {
        didSet {
            ctaButton.titleLabel?.font = .Button1()
            ctaButton.backgroundColor = .Tint1
        }
    }
    @IBOutlet weak var totalPriceLabel: UILabel! {
        didSet {
            totalPriceLabel.font = .Body()
        }
    }

    private var messageTitle: String?
    private var messageText: String?

    init(title: String?, message: String?) {
        super.init(nibName: String(describing: RateChangedViewController.self), bundle: nil)

        self.messageTitle = title
        self.messageText = message
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .clear
        heading.text = messageTitle
        message.text = messageText
    }

    @IBAction func searchAgainButtonDidTap(_ sender: Any) {
        controllerOutput?.cancelAction(sender: self)
    }

    @IBAction func continueWithRateButtonDidTap(_ sender: Any) {
        controllerOutput?.continueWithRateButtonDidTap(sender: self)
    }
}
