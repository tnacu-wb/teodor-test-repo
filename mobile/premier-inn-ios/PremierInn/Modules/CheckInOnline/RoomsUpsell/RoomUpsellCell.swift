//
//  RoomUpsellCell.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 14.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class RoomUpsellCell: UITableViewCell {
    static let reuseIdentifier = "RoomUpsellCell"

    private let stackView: UIStackView = {
        let stackView = UIStackView()
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.spacing = 4
        stackView.axis = .vertical
        stackView.alignment = .leading
        stackView.distribution = .fill
        stackView.isLayoutMarginsRelativeArrangement = true
        stackView.directionalLayoutMargins = .init(top: 16, leading: 16, bottom: 16, trailing: 16)
        stackView.layer.cornerRadius = 3
        stackView.layer.borderWidth = 1
        stackView.layer.borderColor = UIColor.TintL3.cgColor
        return stackView
    }()

    private let roomTitleLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = .BaseBlack
        label.font = .Heading4_Bold()
        return label
    }()

    private let paragraphStyle: NSParagraphStyle = {
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineHeightMultiple = 1.4

        return paragraphStyle
    }()

    private let guestsTitleLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = .TintD2
        label.font = .BodySmall()
        label.numberOfLines = 0
        return label
    }()

    private lazy var messageView = CiolUpsellMessageView()

    private let packagesTitleLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = .TintD2
        label.font = .BodySmall()
        label.numberOfLines = 0
        return label
    }()

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none

        setupViews()
        setupConstraints()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    func configure(roomNumber: Int, guests: [String], addedDescriptions: [String], config: CiolUpsellCellSetup) {
        roomTitleLabel.text = PILocalizedString("Room") + " \(roomNumber + 1)"

        let attributedGuests = NSAttributedString(
            string: guests.joined(separator: "\n"),
            attributes: [.paragraphStyle: paragraphStyle]
        )
        guestsTitleLabel.attributedText = attributedGuests
        let upsellsText = NSAttributedString(
            string: addedDescriptions.joined(separator: "\n"),
            attributes: [.paragraphStyle: paragraphStyle]
        )
        packagesTitleLabel.attributedText = upsellsText
        if config.enabled == true {
            messageView.isHidden = true
            roomTitleLabel.textColor = .BaseBlack
            guestsTitleLabel.textColor = .TintD2
            packagesTitleLabel.textColor = .TintD2
        } else {
            messageView.configure(config: config)
            messageView.isHidden = false
            roomTitleLabel.textColor = .BaseBlack.withAlphaComponent(0.4)
            guestsTitleLabel.textColor = .TintD2.withAlphaComponent(0.4)
            packagesTitleLabel.textColor = .TintD2.withAlphaComponent(0.4)
        }
    }

    private func setupViews() {
        addSubview(stackView)

        stackView.addArrangedSubview(roomTitleLabel)
        stackView.addArrangedSubview(guestsTitleLabel)
        stackView.addArrangedSubview(packagesTitleLabel)
        stackView.addArrangedSubview(messageView)
    }

    private func setupConstraints() {
        NSLayoutConstraint.activate([
            stackView.topAnchor.constraint(equalTo: topAnchor),
            stackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 16),
            stackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -16),
            stackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -4)
        ])
    }
}
