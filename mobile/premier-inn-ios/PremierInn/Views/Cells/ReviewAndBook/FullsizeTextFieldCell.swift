//
//  FullsizeTextFieldCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 31/08/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class FullsizeTextFieldCell: FormekaTextFieldCell {
    override var textField: UITextField! {
        get {
            fullLengthTextField
        }
        set {
        }
    }

    override var errorLabel: UILabel? {
        get {
            fullLengthErrorLabel
        }
        set {
        }
    }

    @IBOutlet weak var fullLengthTextField: UITextField! {
        didSet {
            fullLengthTextField.font = .Body()
            fullLengthTextField.layer.borderWidth = 2
            fullLengthTextField.layer.borderColor = UIColor.TintD2.cgColor
            fullLengthTextField.textAlignment = .left

            let spacerView = UIView(frame: CGRect(x: 0, y: 0, width: 15, height: 10))
            fullLengthTextField.leftViewMode = .always
            fullLengthTextField.leftView = spacerView
        }
    }

    @IBOutlet weak var fullLengthErrorLabel: UILabel? {
        didSet {
            fullLengthErrorLabel?.text = nil
            fullLengthErrorLabel?.alpha = 0
        }
    }

    @IBOutlet weak var fullLenghtTextFieldWidthConstrant: NSLayoutConstraint!

    override func prepareForReuse() {
        super.prepareForReuse()

        fullLenghtTextFieldWidthConstrant.constant = 335
        fullLengthTextField.placeholder = ""
        fullLengthTextField.keyboardType = .default
        delegate = nil
        textField.isSecureTextEntry = false
        fullLengthTextField.text = nil
    }

    override var errorMessage: String? {
        didSet {
            fullLengthTextField.layer.borderColor = errorMessage == nil ? UIColor.TintD2.cgColor : UIColor.Tint8.cgColor
        }
    }
}
