//
//  ErrorCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 21/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class ErrorCell: SimpleSeparatorsCell {
    @IBOutlet weak var errorLabel: UILabel! {
        didSet {
            errorLabel.font = .Body()
        }
    }
    @IBOutlet weak var errorImageView: UIImageView!
    @IBOutlet weak var errorView: UIView!
    @IBOutlet weak var topConstraint: NSLayoutConstraint!
    @IBOutlet weak var leftConstraint: NSLayoutConstraint!
    @IBOutlet weak var bottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var rightConstraint: NSLayoutConstraint!

    var padding: UIEdgeInsets = .zero {
        didSet {
            topConstraint.constant = padding.top
            leftConstraint.constant = padding.left
            bottomConstraint.constant = padding.bottom
            rightConstraint.constant = padding.right
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        errorLabel.textColor = .Tint8
        errorView.layer.borderColor = UIColor.Tint8.cgColor
        errorView.layer.borderWidth = 1.0
    }
}
