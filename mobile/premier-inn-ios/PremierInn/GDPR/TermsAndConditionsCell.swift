//
//  TermsAndConditionsCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 22/10/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import Formeka
import UIKit

class TermsAndConditionsCell: SimpleSeparatorsCell, FormekaErrorCell {
    @IBOutlet weak var message: UILabel!
    @IBOutlet weak var toggleSwitch: UISwitch! {
        didSet {
            toggleSwitch.addTarget(self, action: #selector(switchToggled), for: UIControl.Event.valueChanged)
        }
    }
    @IBOutlet public weak var errorLabel: UILabel? {
        didSet {
            errorLabel?.text = nil
            errorLabel?.alpha = 0
        }
    }

    var errorMessage: String? {
        didSet {
            errorLabel?.text = errorMessage
            errorLabel?.alpha = errorMessage == nil ? 0 : 1
        }
    }
    var toggled: ((Bool) -> Void)?

    override public func prepareForReuse() {
        super.prepareForReuse()

        errorLabel?.text = nil
        errorLabel?.alpha = 0
    }

    @objc private func switchToggled() {
        toggled?(toggleSwitch.isOn)
    }
}
