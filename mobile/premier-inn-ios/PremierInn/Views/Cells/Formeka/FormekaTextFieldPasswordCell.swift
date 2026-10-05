//
//  FormekaTextField+extension.swift
//  PremierInn
//
//  Created by Simon Antoine on 09/04/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import Foundation
import Formeka
import UIKit

final class FormekaTextFieldPasswordCell: FormekaTextFieldCell {
    @IBOutlet private weak var showButtonView: PasswordVisibilityButton!

    @IBOutlet weak var passwordField: UITextField! {
        didSet {
            ContentsquareConfig.mask(view: passwordField)
        }
    }
    func setShowButton() {
        showButtonView.titleColor = .BasePurple
        showButtonView.titleFont = UIFont.BodySmall()
        showButtonView.setTitle(
            PILocalizedString("passwordVisibilityShowTitle", comment: "Password visibility button: show"),
            for: .normal
        )
        showButtonView.textfield = textField
        showButtonView.accessibilityIdentifier = "passwordVisibilityButtonAcc"
    }
}
