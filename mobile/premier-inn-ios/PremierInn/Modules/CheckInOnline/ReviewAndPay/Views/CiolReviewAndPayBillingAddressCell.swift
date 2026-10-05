//
//  CiolReviewAndPayBillingAddressCell.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 02.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class CiolReviewAndPayBillingAddressCell: SimpleSeparatorsCell {
    @IBOutlet weak var addressLabel: UILabel! {
        didSet {
            addressLabel.font = .Body()
            addressLabel.textColor = .BaseBlack
        }
    }

    @IBOutlet weak var billingAddressSwitch: UISwitch!

    var didToggleSwitch: ((_ isOn: Bool) -> Void)?

    func customise(with address: String) {
        addressLabel.text = address
    }

    @IBAction func toggleAddressSwitch(_ sender: UISwitch) {
        didToggleSwitch?(billingAddressSwitch.isOn)
    }
}
