//
//  CiolStoredPaymentMethodView.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 01.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class CiolStoredPaymentMethodView: UIView {
    @IBOutlet weak var cardTypeAndNumberDescription: UILabel!
    @IBOutlet weak var cardTypeIcon: UIImageView!
    @IBOutlet weak var cardholder: UILabel!
    @IBOutlet weak var expiry: UILabel!
    @IBOutlet weak var radioButton: RadioButtonView!
    @IBOutlet weak var cardTag: UILabel!

    var action: (() -> Void)? {
        didSet {
            addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(userDidTapOnView)))
        }
    }

    @objc private func userDidTapOnView() {
        action?()
    }

    func customise(with paymentViewModel: PaymentMethodViewModelType, isFirst: Bool) {
        cardTypeAndNumberDescription.text = paymentViewModel.cardName + " " + (paymentViewModel.maskedPAN ?? "")
        cardholder.text = paymentViewModel.cardholder
        expiry.text = PILocalizedString("paymentCardFutureExpiration") + " " + "\(paymentViewModel.expiry?.trimmingCharacters(in: .whitespaces) ?? "")"
        radioButton.isSelected = paymentViewModel.selected
        cardTag.text = paymentViewModel.type.description ?? ""
        cardTag.isHidden = paymentViewModel.type.description?.isEmpty ?? true

        if let url = paymentViewModel.imageUrls?.first {
            cardTypeIcon.setImage(with: url, transition: true) { [weak self] error in
                if error != nil {
                    self?.cardTypeIcon.image = UIImage(named: "imageLoadError")
                }
            }
        } else {
            cardTypeIcon.image = nil
        }

        setupAccessibility(using: paymentViewModel)

        layer.borderWidth = 1
        layer.borderColor = paymentViewModel.selected ? UIColor.Tint1.cgColor : UIColor.TintL1.cgColor

        if isFirst {
            layer.cornerRadius = 4
            layer.maskedCorners = [.layerMaxXMinYCorner, .layerMinXMinYCorner]
        }
    }

    private func setupAccessibility(using viewModel: PaymentMethodViewModelType) {
        accessibilityIdentifier = viewModel.type.getAccessbilityIdentifier
        isAccessibilityElement = true
        accessibilityLabel = viewModel.accessibilityLabel
        accessibilityTraits = .button

        if viewModel.selected {
            accessibilityTraits.insert(.selected)
        }

        cardTypeAndNumberDescription.isAccessibilityElement = false
        cardTypeIcon.isAccessibilityElement = false
        cardholder.isAccessibilityElement = false
        expiry.isAccessibilityElement = false
        radioButton.isAccessibilityElement = false
        cardTag.isAccessibilityElement = false
    }
}
