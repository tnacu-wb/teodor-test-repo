//
//  PaymentOptionActionView.swift
//  PremierInn
//
//  Created by Freddie Parks on 04/02/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import UIKit

final class PaymentOptionActionView: UIView {
    @IBOutlet weak var separatorView: UIView!
    @IBOutlet weak var horizontalDividerLeftSpacingConstraint: NSLayoutConstraint!
    @IBOutlet weak var action: UILabel!

    var selected: (() -> Void)?

    override func awakeFromNib() {
        super.awakeFromNib()

        backgroundColor = .clear
        addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(selectedView)))
    }

    func configure(
        title: String,
        accessibilityLabel: String,
        selected: @escaping () -> Void
    ) {
        self.selected = selected

        separatorView.isHidden = true
        action.font = UIFont.Body_Medium()
        action.textColor = .BasePurple
        action.text = title
        action.isAccessibilityElement = false

        isAccessibilityElement = true
        self.accessibilityLabel = accessibilityLabel
        accessibilityTraits = .button
    }

    @objc private func selectedView() {
        selected?()
    }

    override func accessibilityActivate() -> Bool {
        selected?()
        return true
    }
}
