//
//  ExtraUpsellCell.swift
//  PremierInn
//
//  Created by Santa Gurung on 29/07/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class ExtraUpsellCell: UITableViewCell {
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .Heading4_Semibold()
        }
    }
    @IBOutlet weak var priceLabel: UILabel! {
        didSet {
            priceLabel.font = .Body()
        }
    }
    @IBOutlet weak var descriptionLabel: UILabel! {
        didSet {
            descriptionLabel.font = .BodySmall()
        }
    }
    @IBOutlet weak var switchToggle: UISwitch! {
        didSet {
            switchToggle.addTarget(self, action: #selector(switchToggled), for: .valueChanged)
        }
    }

    var toggled: ((Bool) -> Void)?

    @objc private func switchToggled() {
        toggled?(switchToggle.isOn)
    }

    func updateSwitchState(toggle: Bool) {
        switchToggle.isOn = toggle
    }
}
