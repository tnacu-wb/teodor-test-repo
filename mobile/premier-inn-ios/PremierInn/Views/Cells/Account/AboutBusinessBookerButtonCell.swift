//
//  AboutBusinessBookerButtonCell.swift
//  PremierInn
//
//  Created by Filippo Minelle on 06/11/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import UIKit
import Formeka

protocol AboutBusinessBookerButtonCellDelegate: AnyObject {
    func buttonDidTap(cell: AboutBusinessBookerButtonCell)
}

class AboutBusinessBookerButtonCell: SimpleSeparatorsCell {
    // MARK: - Properties

    weak var delegate: AboutBusinessBookerButtonCellDelegate?
    var borderColor: UIColor = .BasePurple

    // MARK: - Views

    @IBOutlet weak var button: RoundedCornersButton! {
        didSet {
            button.accessibilityIdentifier = AccessibilityIdentifiers.Login.moreAboutBusinessBookerButton
            button.isAccessibilityElement = true
        }
    }

    @IBAction func buttonDidTap(_ sender: UIButton) {
        delegate?.buttonDidTap(cell: self)
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        button.layer.borderColor = borderColor.cgColor
    }
}
