//
//  CVVInputCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 07/08/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class CVVInputCell: FormekaTextFieldCell {
    override var textField: UITextField! {
        get {
            cvvTextField
        }
        set {
        }
    }

    override var errorLabel: UILabel? {
        get {
            cvvErrorLabel
        }
        set {
        }
    }

    @IBOutlet weak var cvvTextField: UITextField! {
        didSet {
            cvvTextField.layer.borderWidth = 2
            cvvTextField.layer.borderColor = UIColor.TintD2.cgColor
            cvvTextField.textAlignment = .left

            let spacerView = UIView(frame: CGRect(x: 0, y: 0, width: 15, height: 10))
            cvvTextField.leftViewMode = .always
            cvvTextField.leftView = spacerView
        }
    }
    @IBOutlet weak var cvvInfoLabel: UILabel! {
        didSet {
            cvvInfoLabel.font = .SubtextSmall()
        }
    }
    @IBOutlet weak var cvvErrorLabel: UILabel? {
        didSet {
            cvvErrorLabel?.text = nil
            cvvErrorLabel?.alpha = 0
        }
    }

    override var errorMessage: String? {
        didSet {
            cvvTextField.layer.borderColor = errorMessage == nil ? UIColor.TintD2.cgColor : UIColor.Tint8.cgColor
        }
    }
}
