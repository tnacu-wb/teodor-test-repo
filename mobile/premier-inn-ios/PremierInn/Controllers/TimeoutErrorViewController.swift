//
//  TimeoutErrorViewController.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

protocol TimeoutErrorViewControllerOutput: AnyObject {
    func tryAgainButtonDidTap(sender: UIViewController)
}

class TimeoutErrorViewController: UIViewController {
    @IBOutlet weak var heading: UILabel! {
        didSet {
            heading.accessibilityIdentifier = "errorAlertTitle"
            heading.accessibilityTraits.insert(.header)
            heading.font = .Heading3_Semibold()
        }
    }
    @IBOutlet weak var message: UILabel! {
        didSet {
            message.font = .BodySmall()
        }
    }
    @IBOutlet weak var ctaButton: FadeOnHighlightButton! {
        didSet {
            ctaButton.titleLabel?.font = .Button1()
            ctaButton.backgroundColor = .Tint1
            ctaButton.setTitleColor(.BaseWhite, for: .normal)
            ctaButton.setTitle(PILocalizedString("ctaButtonTryAgain"), for: .normal)
        }
    }
    @IBOutlet var icon: UIImageView!
    @IBOutlet weak var optionalCTAButton: FadeOnHighlightButton! {
        didSet {
            optionalCTAButton.titleLabel?.font = .Button1()
            optionalCTAButton.backgroundColor = .Tint1
            optionalCTAButton.setTitleColor(.BaseWhite, for: .normal)
        }
    }

    weak var controllerOutput: TimeoutErrorViewControllerOutput?

    var overrideCtaCompletion: ((_ controller: UIViewController) -> Void)?
    var optionalCompletion: ((_ controller: UIViewController) -> Void)?

    private var messageTitle: String?
    private var messageText: String?
    private var messageButton: String?
    private var accessibilityButtonLabel: String?

    init(title: String?, message: String?, button: String?, accessibilityButtonLabel: String?) {
        super.init(nibName: String(describing: TimeoutErrorViewController.self), bundle: nil)

        self.messageTitle = title
        self.messageText = message
        self.messageButton = button
        self.accessibilityButtonLabel = accessibilityButtonLabel
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .clear
        heading.text = messageTitle
        message.text = messageText
        if let messageButton = messageButton {
            ctaButton.setTitle(messageButton, for: .normal)
            ctaButton.accessibilityLabel = accessibilityButtonLabel ?? messageButton
        }
    }

    @IBAction func tryAgainButtonDidTap(_ sender: Any) {
        if let overrideCtaCompletion = overrideCtaCompletion {
            overrideCtaCompletion(self)
            return
        }

        controllerOutput?.tryAgainButtonDidTap(sender: self)
    }
    @IBAction func optionalButtonDidTap(_ sender: Any) {
        optionalCompletion?(self)
    }
}
