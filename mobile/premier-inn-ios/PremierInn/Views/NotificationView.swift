//
//  NotificationView.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 04/02/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

protocol NotificationViewInputDelegate: AnyObject {
    func actionTapped(withSenderView senderView: NotificationView?)
    func dismissTapped(withSenderView senderView: NotificationView?)
}

class NotificationView: UIView {
    // MARK: - Properties

    weak var notificationViewInputDelegate: NotificationViewInputDelegate?

    // MARK: - Views

    @IBOutlet var contentView: UIView!

    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            self.titleLabel.textColor = .BaseWhite
            self.titleLabel.font = UIFont.Body_Semibold()
        }
    }
    @IBOutlet weak var messageLabel: UILabel! {
        didSet {
            self.messageLabel.textColor = .BaseWhite
            self.messageLabel.font = UIFont.Body()
        }
    }
    @IBOutlet weak var actionButton: UIButton! {
        didSet {
            self.actionButton.setTitleColor(.BaseWhite, for: .normal)
            self.actionButton.titleLabel?.font = UIFont.Body_Semibold()
            self.actionButton.layer.borderColor = UIColor.BaseWhite.cgColor
            self.actionButton.layer.cornerRadius = 4
            self.actionButton.layer.borderWidth = 1
        }
    }
    @IBOutlet weak var notificationIcon: UIImageView! {
        didSet {
            self.notificationIcon.tintColor = .BaseWhite
        }
    }
    @IBOutlet weak var dismissButton: UIButton! {
        didSet {
            self.dismissButton.tintColor = .BaseWhite
            self.dismissButton.setImage(UIImage(imageLiteralResourceName: "cross"), for: .normal)
        }
    }

    // MARK: - Initialisation

    override init(frame: CGRect) {
        super.init(frame: frame)

        initSubviews()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)

        initSubviews()
    }

    func initSubviews() {
        let nib = UINib(nibName: String(describing: NotificationView.self), bundle: nil)
        nib.instantiate(withOwner: self, options: nil)
        contentView.frame = bounds
        addSubview(contentView)
    }

    // MARK: - Actions

    public func hide() {
        contentView.isHidden = true
        isHidden = true
    }

    public func show() {
        contentView.isHidden = false
        isHidden = false
    }

    @IBAction func dismissButtonTapped() {
        self.notificationViewInputDelegate?.dismissTapped(withSenderView: self)
    }

    @IBAction func actionButtonTapped() {
        self.notificationViewInputDelegate?.actionTapped(withSenderView: self)
    }
}
