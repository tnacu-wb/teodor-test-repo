//
//  PaymentMethodCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

final class PaymentMethodCell: UITableViewCell {
    private enum Constants {
        static let paymentMethodCellIdentifier = "paymentMethodCellAcc"
        static let cardNameLabelIdentifier = "cardNameLabelAcc"
        static let cardNumberLabelIdentifier = "cardNumberLabelAcc"
        static let cardHolderNameLabelIdentifier = "cardHolderNameLabelAcc"
        static let cardExpiryDateLabelIdentifier = "cardExpiryDateLabelAcc"
        static let cardUsageLabelIdentifier = "cardUsageLabelAcc"
        static let radioButtonIdentifier = "radioButtonAcc"

        static let deleteTitle = PILocalizedString("delete")
    }

    private var deleteButtonAction: (() -> Void)?
    private var linkAction: ((PaymentMethodsLinkAction) -> Void)?

    @IBOutlet private weak var cardImageView: UIImageView! {
        didSet {
            cardImageView.isAccessibilityElement = false
        }
    }

    @IBOutlet private weak var actionsView: UIStackView! {
        didSet {
            actionsView.isAccessibilityElement = false
        }
    }

    @IBOutlet private weak var radioButton: RadioButtonView! {
        didSet {
            radioButton.isAccessibilityElement = false
        }
    }

    @IBOutlet private weak var containerView: UIView! {
        didSet {
            backgroundColor = .whiteTwo
            contentView.backgroundColor = .whiteTwo

            containerView.backgroundColor = .white
            containerView.layer.cornerRadius = 4
            containerView.layer.borderColor = UIColor.ColourLD3.cgColor
            containerView.layer.borderWidth = 1.0
            containerView.isAccessibilityElement = false
        }
    }

    @IBOutlet private weak var deleteButton: UIButton! {
        didSet {
            deleteButton.isAccessibilityElement = true
            configureDeleteButton()
        }
    }

    @IBOutlet private weak var cardNameLabel: UILabel! {
        didSet {
            cardNameLabel.isAccessibilityElement = false
            cardNameLabel.textColor = .BasePurple
            cardNameLabel.font = UIFont.Heading2_ExtraBold()
        }
    }

    @IBOutlet private weak var cardNumberLabel: UILabel! {
        didSet {
            cardNumberLabel.isAccessibilityElement = false
            cardNumberLabel.textColor = .TintD1
            cardNumberLabel.font = UIFont.Body()
        }
    }

    @IBOutlet private weak var cardHolderNameLabel: UILabel! {
        didSet {
            cardHolderNameLabel.isAccessibilityElement = false
            cardHolderNameLabel.textColor = .TintD1
            cardHolderNameLabel.font = UIFont.Body()
        }
    }

    @IBOutlet private weak var cardExpiryDateLabel: UILabel! {
        didSet {
            cardExpiryDateLabel.isAccessibilityElement = false
            cardExpiryDateLabel.textColor = .TintD1
            cardExpiryDateLabel.font = UIFont.Body()
        }
    }

    @IBOutlet private weak var cardUsageLabel: UILabel! {
        didSet {
            cardUsageLabel.isAccessibilityElement = false
            cardUsageLabel.font = .BodySmall()
            cardUsageLabel.superview?.backgroundColor = .TintL1
            cardUsageLabel.superview?.layer.cornerRadius = 2
        }
    }

    @IBOutlet private weak var cardInfoMessageContainer: UIView! {
        didSet {
            cardInfoMessageContainer.isAccessibilityElement = false
            cardInfoMessageContainer.backgroundColor = .alertOrangeTint
            cardInfoMessageContainer.layer.cornerRadius = 4
            cardInfoMessageContainer.layer.borderColor = UIColor.alertOrange.cgColor
            cardInfoMessageContainer.layer.borderWidth = 1
            cardInfoMessageContainer.isHidden = true
        }
    }

    @IBOutlet private weak var cardInfoPibaBanner: UIView! {
        didSet {
            cardInfoPibaBanner.isAccessibilityElement = false
            cardInfoPibaBanner.backgroundColor = .Tint3
            cardInfoPibaBanner.layer.cornerRadius = 4
            cardInfoPibaBanner.layer.borderColor = UIColor.Tint2.cgColor
            cardInfoPibaBanner.layer.borderWidth = 1
            cardInfoPibaBanner.isHidden = true
        }
    }

    @IBOutlet private weak var cardInfoMessageLabel: UILabel! {
        didSet {
            cardInfoMessageLabel.isAccessibilityElement = false
            cardInfoMessageLabel.font = .BodySmall()
            cardInfoMessageLabel.textColor = .TintD1
        }
    }

    @IBOutlet private weak var cardInfoMessageIcon: UIImageView! {
        didSet {
            cardInfoMessageIcon.isAccessibilityElement = false
            cardInfoMessageIcon.tintColor = UIColor.alertOrange.withAlphaComponent(1.0)
        }
    }

    @IBOutlet private weak var pibaCardInfoLabel: UILabel! {
        didSet {
            pibaCardInfoLabel.isAccessibilityElement = false
            pibaCardInfoLabel.font = .BodySmall()
            pibaCardInfoLabel.textColor = .TintD1
        }
    }

    @IBOutlet private weak var pibaInfoIcon: UIImageView! {
        didSet {
            pibaInfoIcon.isAccessibilityElement = false
            pibaInfoIcon.tintColor = UIColor.Tint2
        }
    }

    @IBOutlet private weak var containerBottomConstraint: NSLayoutConstraint!

    override func prepareForReuse() {
        super.prepareForReuse()

        isAccessibilityElement = false
        contentView.isAccessibilityElement = false
        containerView.isAccessibilityElement = false
        accessibilityElements = nil

        deleteButtonAction = nil
        linkAction = nil

        actionsView.arrangedSubviews.forEach {
            actionsView.removeArrangedSubview($0)
            $0.removeFromSuperview()
        }
    }

    func configure(
        with section: PaymentMethodsCardSection,
        accessibilityLabel: String,
        onDelete: @escaping () -> Void,
        onLinkSelected: @escaping (PaymentMethodsLinkAction) -> Void
    ) {
        deleteButtonAction = onDelete
        linkAction = onLinkSelected

        configureContent(with: section)

        configureActions(
            with: section.links,
            accessibilityLabel: accessibilityLabel
        )

        configureAccessibility(
            with: section,
            accessibilityLabel: accessibilityLabel,
            accessibilityIdentifierSuffix: section.cardName ?? ""
        )

        if !section.selectable && section.isBookingFlow {
            disable()
        }
    }

    private func configureContent(with section: PaymentMethodsCardSection) {
        cardNameLabel.text = section.cardName
        cardNumberLabel.text = section.cardHiddenNumber
        cardHolderNameLabel.text = section.cardHolderName
        cardExpiryDateLabel.text = section.cardExpiration
        cardUsageLabel.text = section.usageDescription

        if let url = section.cardImageURL {
            cardImageView.setImage(with: url)
        } else {
            cardImageView.image = nil
        }

        radioButton.isHidden = !section.selectable
        radioButton.isSelected = section.selected

        contentView.backgroundColor = .clear
        backgroundColor = .clear

        deleteButton.isHidden = section.isDeleteHidden
        deleteButton.accessibilityLabel = [Constants.deleteTitle, section.accessibilityLabel]
            .compactMap { $0 }
            .filter { $0.isNotEmpty }
            .joined(separator: ", ")

        configureDeleteButton()

        updateCardInfo(with: section.cardInfoMessage)
        updatePibaCardBanner(string: section.pibaMessaging)
    }

    private func configureDeleteButton() {
        deleteButton.configurationUpdateHandler = { button in
            var config = UIButton.Configuration.plain()
            config.title = Constants.deleteTitle
            config.titleTextAttributesTransformer = UIConfigurationTextAttributesTransformer { attribute in
                var title = attribute
                title.font = UIFont.Body_Medium()
                title.foregroundColor = .BasePurple
                return title
            }
            config.contentInsets = NSDirectionalEdgeInsets(top: 0, leading: 0, bottom: 0, trailing: 0)
            button.configuration = config
        }
    }

    private func configureActions(
        with links: [PaymentMethodsLink],
        accessibilityLabel: String
    ) {
        actionsView.arrangedSubviews.forEach {
            actionsView.removeArrangedSubview($0)
            $0.removeFromSuperview()
        }

        links.forEach { link in
            guard let actionView: PaymentOptionActionView = UIView
                .fromNib(nibName: String(describing: PaymentOptionActionView.self)) else {
                return
            }

            let title = link.title
            let accessibilityLabel = [link.title, accessibilityLabel]
                .filter { $0.isNotEmpty }
                .joined(separator: ", ")

            actionView.configure(
                title: title,
                accessibilityLabel: accessibilityLabel,
                selected: { [weak self] in
                    self?.linkAction?(link.action)
                }
            )

            actionsView.addArrangedSubview(actionView)
        }
    }

    @IBAction private func deleteButtonTapped(_ sender: Any) {
        deleteButtonAction?()
    }

    private func updateCardInfo(with info: String?) {
        guard let info else {
            cardInfoMessageLabel.text = nil
            cardInfoMessageContainer.isHidden = true
            cardInfoMessageContainer.isAccessibilityElement = false
            return
        }

        cardInfoMessageLabel.text = info
        cardInfoMessageContainer.isHidden = false
    }

    private func updatePibaCardBanner(string: String?) {
        guard let string else {
            pibaCardInfoLabel.text = nil
            cardInfoPibaBanner.isHidden = true
            cardInfoPibaBanner.isAccessibilityElement = false
            return
        }

        pibaCardInfoLabel.text = string
        cardInfoPibaBanner.isHidden = false
    }

    /// Adds alpha (default `0.5`) to all card info in the cell.
    private func disable(_ alpha: CGFloat = CGFloat(0.5)) {
        if let newImage = cardImageView.image?.alpha(alpha) {
            cardImageView.image = newImage
        }

        cardNameLabel.textColor = cardNameLabel.textColor.withAlphaComponent(alpha)
        cardNumberLabel.textColor = cardNumberLabel.textColor.withAlphaComponent(alpha)
        cardHolderNameLabel.textColor = cardHolderNameLabel.textColor.withAlphaComponent(alpha)
        cardExpiryDateLabel.textColor = cardExpiryDateLabel.textColor.withAlphaComponent(alpha)
        cardUsageLabel.textColor = cardUsageLabel.textColor.withAlphaComponent(alpha)
        cardUsageLabel.superview?.backgroundColor = cardUsageLabel.superview?.backgroundColor?.withAlphaComponent(alpha)
    }
}

private extension PaymentMethodCell {
    func configureAccessibility(
        with section: PaymentMethodsCardSection,
        accessibilityLabel: String,
        accessibilityIdentifierSuffix: String
    ) {
        isAccessibilityElement = false
        contentView.isAccessibilityElement = false

        accessibilityIdentifier = Constants.paymentMethodCellIdentifier + accessibilityIdentifierSuffix
        setAccessibliltyIdentifiers(using: accessibilityIdentifierSuffix)

        containerView.isAccessibilityElement = true
        containerView.accessibilityLabel = accessibilityLabel
        containerView.accessibilityTraits = section.selectable ? .button : .staticText

        if section.selected {
            containerView.accessibilityTraits.insert(.selected)
        }

        deleteButton.isAccessibilityElement = !deleteButton.isHidden

        cardInfoPibaBanner.isAccessibilityElement = !cardInfoPibaBanner.isHidden
        cardInfoPibaBanner.accessibilityLabel = pibaCardInfoLabel.text

        cardInfoMessageContainer.isAccessibilityElement = !cardInfoMessageContainer.isHidden
        cardInfoMessageContainer.accessibilityLabel = cardInfoMessageLabel.text

        setupAccessibilityElements()
    }

    func setAccessibliltyIdentifiers(using suffix: String) {
        cardNameLabel.accessibilityIdentifier = Constants.cardNameLabelIdentifier + suffix
        cardNumberLabel.accessibilityIdentifier = Constants.cardNumberLabelIdentifier + suffix
        cardHolderNameLabel.accessibilityIdentifier = Constants.cardHolderNameLabelIdentifier + suffix
        cardExpiryDateLabel.accessibilityIdentifier = Constants.cardExpiryDateLabelIdentifier + suffix
        cardUsageLabel.accessibilityIdentifier = Constants.cardUsageLabelIdentifier + suffix
        radioButton.accessibilityIdentifier = Constants.radioButtonIdentifier + suffix
    }

    func setupAccessibilityElements() {
        var elements: [NSObject] = [containerView]

        if !deleteButton.isHidden {
            elements.append(deleteButton)
        }

        actionsView.arrangedSubviews.forEach { actionView in
            guard !actionView.isHidden else { return }
            actionView.isAccessibilityElement = true
            elements.append(actionView)
        }

        if !cardInfoPibaBanner.isHidden {
            elements.append(cardInfoPibaBanner)
        }

        if !cardInfoMessageContainer.isHidden {
            elements.append(cardInfoMessageContainer)
        }

        accessibilityElements = elements
    }
}
