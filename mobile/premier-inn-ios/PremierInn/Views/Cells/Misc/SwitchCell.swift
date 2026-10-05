//
//  SwitchCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 30/05/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class SwitchCell: SimpleSeparatorsCell {
    @IBOutlet weak var message: UILabel! {
        didSet {
            message.font = UIFont.Body()
        }
    }
    @IBOutlet weak var toggleSwitch: UISwitch! {
        didSet {
            toggleSwitch.addTarget(self, action: #selector(switchToggled), for: .valueChanged)
        }
    }

    var toggled: ((Bool) -> Void)?

    @objc private func switchToggled() {
        toggled?(toggleSwitch.isOn)
    }
}
