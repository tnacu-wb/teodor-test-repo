//
//  BiometricButtonCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 10/07/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka

protocol BiometricButtonCellDelegate: AnyObject {
    func buttonDidTap(cell: BiometricButtonCell)
}

class BiometricButtonCell: SimpleSeparatorsCell {
    weak var delegate: BiometricButtonCellDelegate?

    @IBOutlet weak var button: RoundedCornersButton! {
        didSet {
            button.accessibilityIdentifier = AccessibilityIdentifiers.Login.touchIdLink
            button.isAccessibilityElement = true
        }
    }

    @IBAction func buttonDidTap(_ sender: UIButton) {
        delegate?.buttonDidTap(cell: self)
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        button.layer.borderColor = UIColor.lightGray.cgColor
    }
}
