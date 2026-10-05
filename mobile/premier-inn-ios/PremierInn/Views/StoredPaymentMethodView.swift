//
//  StoredPaymentMethodView.swift
//  PremierInn
//
//  Created by Freddie Parks on 26/03/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

class StoredPaymentMethodView: UIView {
    private enum Constants {
        static let fallbackErrorImageName = "imageLoadError"
    }

    // MARK: - IBOutlets

    @IBOutlet weak var cardTypeAndNumberDescription: UILabel!
    @IBOutlet weak var cardTypeIcon: UIImageView!
    @IBOutlet weak var cardholder: UILabel!
    @IBOutlet weak var expiry: UILabel!
    @IBOutlet weak var radioButton: RadioButtonView!
    @IBOutlet weak var cardTagTopConstraint: NSLayoutConstraint!
    @IBOutlet weak var cardTagHeightConstraint: NSLayoutConstraint!

    @IBOutlet private weak var cardTag: UILabel! {
        didSet {
            cardTag.layer.cornerRadius = 12
            cardTag.layer.borderWidth = 1
            cardTag.layer.borderColor = UIColor.marketingMaroon.cgColor

            cardTag.textColor = .BasePurple
        }
    }

    @IBOutlet private weak var separatorView: UIView! {
       didSet {
           separatorView.backgroundColor = .greyBorder
       }
    }

    // MARK: - Properties

    var paymentMethodType: PaymentMethodType? {
        didSet {
            guard let paymentMethodType = paymentMethodType else { return hideTag() }

            switch paymentMethodType {
            case .storedCompanyBB, .storedPersonalBB:
                guard let tagString = paymentMethodType.description else { return hideTag() }
                showTag(with: tagString)
            default:
                hideTag()
            }
        }
    }

    var action: (() -> Void)? {
        didSet {
            addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(userDidTapOnView)))
        }
    }

    @objc private func userDidTapOnView() {
        action?()
    }

    private func showTag(with tag: String) {
        cardTag.text = tag
        cardTag.isHidden = false
        cardTagTopConstraint.constant = 7
        cardTagHeightConstraint.constant = 24
    }

    private func hideTag() {
        cardTag.text = nil
        cardTag.isHidden = true
        cardTagTopConstraint.constant = 0
        cardTagHeightConstraint.constant = 0
    }

    func configure(
        with viewModel: PaymentMethodViewModelType,
        shouldShowRadio: Bool,
        isLastCell: Bool
    ) {
        cardTypeAndNumberDescription.text = viewModel.maskedPAN
        cardholder.text = viewModel.cardholder
        expiry.text = viewModel.expiry
        radioButton.isHidden = !shouldShowRadio
        radioButton.isSelected = viewModel.selected
        separatorView.isHidden = isLastCell

        setupAccessibility(with: viewModel)
        accessibilityTraits = .button

        if viewModel.selected {
            accessibilityTraits.insert(.selected)
        }

        setupImageView(url: viewModel.imageUrls?.first)
    }
}

private extension StoredPaymentMethodView {
    func setupImageView(url: URL?) {
        guard let url else {
            cardTypeIcon.image = nil
            return
        }

        cardTypeIcon.setImage(with: url, transition: true) { error in
            if error != nil {
                self.cardTypeIcon.image = UIImage(named: Constants.fallbackErrorImageName)
            }
        }
    }

    func setupAccessibility(with viewModel: PaymentMethodViewModelType) {
        isAccessibilityElement = true

        accessibilityIdentifier = viewModel.type.getAccessbilityIdentifier
        accessibilityLabel = viewModel.accessibilityLabel
        accessibilityTraits = .button

        cardTypeAndNumberDescription.isAccessibilityElement = false
        cardTypeIcon.isAccessibilityElement = false
        cardholder.isAccessibilityElement = false
        expiry.isAccessibilityElement = false
        radioButton.isAccessibilityElement = false
        cardTag.isAccessibilityElement = false
    }
}
