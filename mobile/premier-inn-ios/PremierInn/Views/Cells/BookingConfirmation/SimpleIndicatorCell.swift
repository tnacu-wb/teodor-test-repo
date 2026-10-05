//
//  SimpleIndicatorCell.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 04/09/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import UIKit

class SimpleIndicatorCell: DisclosureCell {
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .Heading4_Bold()
            titleLabel.textColor = .BaseBlack
        }
    }

    @IBOutlet weak var messageLabel: UILabel! {
        didSet {
            messageLabel.font = .Heading4_Regular()
            messageLabel.textColor = .BaseBlack
        }
    }

    @IBOutlet weak var titleLeadingConstraint: NSLayoutConstraint!
    @IBOutlet weak var messageLeadingConstraint: NSLayoutConstraint!

    override func awakeFromNib() {
        super.awakeFromNib()
        selectionStyle = .none
    }
}
