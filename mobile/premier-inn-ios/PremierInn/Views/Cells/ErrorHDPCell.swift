//
//  ErrorHDPCell.swift
//  PremierInn
//
//  Created by andrei.cojocaru on 8/14/24.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class ErrorHDPCell: UITableViewCell {
    // MARK: - Style

    private enum Style {
        // warning
        static let warningIcon = UIImage(named: "alert")
        static let warningTintIcon: UIColor = .Tint6
        static let warningContentModeIcon: UIView.ContentMode = . scaleAspectFill
        static let warningBackgroundColor: UIColor = .Tint7
        static let warningBorderColor: CGColor = UIColor.Tint6.withAlphaComponent(0.4).cgColor
        static let warningContentSpacing: CGFloat = 8

        // error
        static let errorIcon = UIImage(named: "errorInfoIcon")
        static let errorTintIcon: UIColor = .Tint8
        static let errorContentModeIcon: UIView.ContentMode = . scaleAspectFit
        static let errorBackgroundColor = UIColor.Tint8.withAlphaComponent(0.2)
        static let errorBorderColor = UIColor.Tint8.cgColor
        static let errorContentSpacing: CGFloat = 4
    }

    // MARK: - Views

    @IBOutlet weak var containerView: UIView! {
        didSet {
            containerView.layer.cornerRadius = 3
            containerView.layer.borderColor = UIColor.Tint8.cgColor
            containerView.layer.borderWidth = 1
        }
    }

    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.setupLabel(
                font: .BodySmall(),
                lineHeightMultiple: 1.15,
                textColor: .TintD1,
                accessibilityIdentifier: "errorHDPTitleLabel"
            )
        }
    }

    @IBOutlet weak var stackView: UIStackView!
    @IBOutlet weak var iconImageView: UIImageView!

    // MARK: - UI Helpers

    override func prepareForReuse() {
        super.prepareForReuse()

        // Reset to the default "error" appearance
        stackView.spacing = Style.errorContentSpacing
        iconImageView.image = Style.errorIcon
        iconImageView.tintColor = Style.errorTintIcon
        iconImageView.contentMode = Style.errorContentModeIcon
        containerView.backgroundColor = Style.errorBackgroundColor
        containerView.layer.borderColor = Style.errorBorderColor
    }

    func styleUIForWarning(with title: String) {
        titleLabel.text = title
        stackView.spacing = Style.warningContentSpacing
        iconImageView.image = Style.warningIcon
        iconImageView.tintColor = Style.warningTintIcon
        iconImageView.contentMode = Style.warningContentModeIcon
        containerView.backgroundColor = Style.warningBackgroundColor
        containerView.layer.borderColor = Style.warningBorderColor
    }
}
