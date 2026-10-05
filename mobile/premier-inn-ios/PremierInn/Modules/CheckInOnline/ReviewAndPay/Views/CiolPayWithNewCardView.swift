//
//  CiolPayWithNewCardView.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 01.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class CiolPayWithNewCardView: CreditCardsListCell {
    @IBOutlet weak var methodType: UILabel!
    @IBOutlet override weak var collectionView: UICollectionView! {
        didSet {
            collectionView.registerCellForNib(with: CreditCardImageCell.self)
            collectionView.delegate = self
            collectionView.dataSource = self
        }
    }
    @IBOutlet weak var radioButton: RadioButtonView!

    var action: (() -> Void)? {
        didSet {
            addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(userDidTapOnView)))
        }
    }

    @objc private func userDidTapOnView() {
        action?()
    }

    func customise(with paymentViewModel: PaymentMethodViewModelType, isFirst: Bool) {
        methodType.text = paymentViewModel.type.description
        cardUrls = paymentViewModel.imageUrls
        radioButton.isSelected = paymentViewModel.selected

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

        methodType.isAccessibilityElement = false
        collectionView.isAccessibilityElement = false
        radioButton.isAccessibilityElement = false
    }
}
