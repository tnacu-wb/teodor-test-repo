//
//  SimpleErrorView.swift
//  PremierInn
//
//  Created by Freddie Parks on 09/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

class SimpleErrorView: UIView {
    @IBOutlet weak var errorLabel: UILabel! {
        didSet {
            errorLabel.font = UIFont.Body()
            errorLabel.textColor = .Tint8
        }
    }
    @IBOutlet weak var errorImageView: UIImageView!
    @IBOutlet weak var errorView: UIView! {
        didSet {
            errorView.layer.borderColor = UIColor.Tint8.cgColor
            errorView.layer.borderWidth = 1.0
        }
    }
    @IBOutlet weak var bottomConstraint: NSLayoutConstraint!
}
