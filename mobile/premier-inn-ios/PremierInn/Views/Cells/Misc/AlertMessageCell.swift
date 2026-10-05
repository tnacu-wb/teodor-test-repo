//
//  CoronavirusMessagingCell.swift
//  PremierInn
//
//  Created by Nick Jones on 26/03/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import UIKit

protocol AlertMessageCellInputDelegate {
    func dismissButtonTapped(withSenderView senderView: AlertMessageCell?)
}

class AlertMessageCell: UITableViewCell {
    // MARK: - Properties

    var alertMessageCellInputDelegate: AlertMessageCellInputDelegate?

    // MARK: - Views

    @IBOutlet weak var warningIcon: UIImageView!
    @IBOutlet weak var warningMessagingLabel: UILabel! {
        didSet {
            self.warningMessagingLabel.text = SettingsManager.sharedInstance.coronavirusBannerMessage
            self.warningMessagingLabel.textColor = .white
            self.warningMessagingLabel.font = UIFont.BodySmall()
        }
    }
    @IBOutlet weak var dismissButton: UIButton!

    var style: AlertStyle = .info {
        didSet {
            contentView.backgroundColor = style.background
            warningMessagingLabel.textColor = style.tint
            warningIcon.tintColor = style.tint
            warningIcon.image = style.icon
            dismissButton.tintColor = style.tint
        }
    }

    // MARK: - Actions

    @IBAction func dismissButtonDidTap() {
        self.alertMessageCellInputDelegate?.dismissButtonTapped(withSenderView: self)
    }

    func update(
        with message: String = SettingsManager.sharedInstance.coronavirusBannerMessage,
        using width: CGFloat? = nil,
        and style: AlertStyle = .info
    ) {
        if let width = width {
            frame.size.width = width
            setNeedsLayout()
            layoutIfNeeded()
        }

        self.style = style
        warningMessagingLabel.text = message

        let bottomPadding = frame.height - warningMessagingLabel.frame.maxY
        let idealSize = warningMessagingLabel.sizeThatFits(CGSize(
            width: warningMessagingLabel.frame.width,
            height: CGFloat.greatestFiniteMagnitude
        ))

        frame.size.height = warningMessagingLabel.frame.minY + idealSize.height + bottomPadding
    }
}
