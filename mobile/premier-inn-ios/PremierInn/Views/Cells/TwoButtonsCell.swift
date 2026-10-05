//
//  TwoButtonsCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 27/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka

protocol TwoButtonsCellDelegate: AnyObject {
    func topButtonDidTap(cell: TwoButtonsCell)
    func bottomButtonDidTap(cell: TwoButtonsCell)
}

class TwoButtonsCell: SimpleSeparatorsCell {
    weak var delegate: TwoButtonsCellDelegate?

    @IBOutlet weak var topButton: UIButton! {
        didSet {
            topButton.backgroundColor = .Tint1
            topButton.setTitleColor(UIColor.BaseWhite, for: .normal)
            topButton.titleLabel?.font = UIFont.Heading3_Semibold()
            topButton.layer.cornerRadius = 5.0
            topButton.isAccessibilityElement = true
        }
    }
    @IBOutlet weak var bottomButton: UIButton! {
        didSet {
            bottomButton.setTitleColor(.BasePurple, for: .normal)
            bottomButton.titleLabel?.font = UIFont.Action1()
            bottomButton.isAccessibilityElement = true
        }
    }
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView! {
        didSet {
            activityIndicator.color = .BaseWhite
        }
    }

    @IBAction func bottomButtonDidTap(_ sender: Any) {
        delegate?.bottomButtonDidTap(cell: self)
    }

    @IBAction func topButtonDidTap(_ sender: UIButton) {
        delegate?.topButtonDidTap(cell: self)
    }
}
