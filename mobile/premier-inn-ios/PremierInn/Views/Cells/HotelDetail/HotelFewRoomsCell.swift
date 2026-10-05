//
//  HotelFewRoomsCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 22/09/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

final class HotelFewRoomsCell: UITableViewCell {
    private enum Constants {
        static let warningIconName = "urgency"
        static let containerMinHeight: CGFloat = 23
        static let iconLabelSpacing: CGFloat = 6
    }
    @IBOutlet weak var lastFewRoomsLabel: UILabel! {
        didSet {
            lastFewRoomsLabel.setupLabel(
                text: PILocalizedString(
                    "hotelDetailsLastFewRooms",
                    comment: "Hotel details: last few rooms title"
                ),
                font: .BodySmall_Bold(),
                textColor: .Tint6,
                accessibilityIdentifier: "lastFewRoomsLabel"
            )
        }
    }

    private lazy var containerView: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = .Tint7
        view.layer.cornerRadius = ViewConstants.CornerRadius.xSmall
        view.layer.masksToBounds = true

        return view
    }()

    private lazy var iconImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.contentMode = .scaleAspectFit
        imageView.tintColor = .Tint6
        if let warningIcon = UIImage(named: Constants.warningIconName) {
            imageView.image = warningIcon.withRenderingMode(.alwaysTemplate)
        }

        return imageView
    }()

    private lazy var messageLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.font = .BodySmall_Bold()
        label.textColor = .Tint6
        label.numberOfLines = 1

        return label
    }()

    private var isUrgencyContainerSetup = false

    private func setupUrgencyContainer() {
        guard !isUrgencyContainerSetup else {
            containerView.isHidden = false
            return
        }
        contentView.addSubview(containerView)
        containerView.addSubview(iconImageView)
        containerView.addSubview(messageLabel)
        NSLayoutConstraint.activate([
            containerView.leadingAnchor.constraint(
                equalTo: contentView.leadingAnchor,
                constant: ViewConstants.Spacing.medium
            ),
            containerView.trailingAnchor.constraint(
                lessThanOrEqualTo: contentView.trailingAnchor,
                constant: -ViewConstants.Spacing.medium
            ),
            containerView.topAnchor.constraint(
                equalTo: contentView.topAnchor,
                constant: ViewConstants.Spacing.small
            ),
            containerView.bottomAnchor.constraint(
                equalTo: contentView.bottomAnchor,
                constant: -ViewConstants.Spacing.small
            ),
            containerView.heightAnchor.constraint(
                greaterThanOrEqualToConstant: Constants.containerMinHeight
            ),
            iconImageView.leadingAnchor.constraint(
                equalTo: containerView.leadingAnchor,
                constant: ViewConstants.Spacing.small
            ),
            iconImageView.centerYAnchor.constraint(equalTo: containerView.centerYAnchor),
            iconImageView.widthAnchor.constraint(equalToConstant: ViewConstants.ImageSize.small),
            iconImageView.heightAnchor.constraint(equalToConstant: ViewConstants.ImageSize.small),
            messageLabel.leadingAnchor.constraint(
                equalTo: iconImageView.trailingAnchor,
                constant: Constants.iconLabelSpacing
            ),
            messageLabel.trailingAnchor.constraint(
                equalTo: containerView.trailingAnchor,
                constant: -ViewConstants.Spacing.small
            ),
            messageLabel.centerYAnchor.constraint(equalTo: containerView.centerYAnchor),
            messageLabel.topAnchor.constraint(
                greaterThanOrEqualTo: containerView.topAnchor,
                constant: ViewConstants.CornerRadius.xSmall
            ),
            messageLabel.bottomAnchor.constraint(
                lessThanOrEqualTo: containerView.bottomAnchor,
                constant: -ViewConstants.CornerRadius.xSmall
            )
        ])

        lastFewRoomsLabel.isHidden = true
        isUrgencyContainerSetup = true
    }

    func configureWithIcon(message: String) {
        setupUrgencyContainer()
        messageLabel.text = message
        containerView.isHidden = false
        lastFewRoomsLabel.isHidden = true

        containerView.isAccessibilityElement = true
        containerView.accessibilityLabel = message
        containerView.accessibilityTraits = .staticText
    }

    func configureWithPlainText(message: String, color: UIColor) {
        containerView.isHidden = true
        lastFewRoomsLabel.isHidden = false
        lastFewRoomsLabel.text = message
        lastFewRoomsLabel.textColor = color
    }

    override func prepareForReuse() {
        super.prepareForReuse()
        containerView.isHidden = true
        lastFewRoomsLabel.isHidden = false
    }
}
