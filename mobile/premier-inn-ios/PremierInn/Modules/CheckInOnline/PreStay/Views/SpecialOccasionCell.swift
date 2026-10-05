//
//  SpecialOccasionCell.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 09.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class SpecialOccasionCell: SimpleSeparatorsCell {
    var showOccasionAlert: Bool = false {
        didSet {
            selectButton.layer.borderColor = showOccasionAlert ? UIColor.Tint8.cgColor : UIColor.TintL1.cgColor
            selectButton.layer.borderWidth = showOccasionAlert ? 2 : 1
            errorAlertStackView.isHidden = !showOccasionAlert
        }
    }

    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .Body_Medium()
            titleLabel.textColor = .BaseBlack
        }
    }

    var didToggleSpecialOccasion: ((_ isOn: Bool) -> Void)?

    @IBOutlet weak var occasionSwitch: UISwitch!

    @IBOutlet weak var selectButton: UIButton! {
        didSet {
            selectButton.setTitleColor(.ColourDL2, for: .normal)
            selectButton.layer.borderColor = UIColor.TintL1.cgColor
            selectButton.layer.borderWidth = 1
            selectButton.layer.cornerRadius = 4
            selectButton.isHidden = true
            if let image = UIImage(named: "arrowDown")?.withTintColor(.ColourDL1) {
                selectButton.addIcon(icon: image, with: .ColourDL1)
            }
        }
    }

    @IBOutlet weak var errorAlertStackView: UIStackView!

    @IBOutlet weak var errorTitleLabel: UILabel! {
        didSet {
            errorTitleLabel.font = .BodySmall_Semibold()
            errorTitleLabel.text = PILocalizedString("ciolSpecialOccasionErrorTitle")
        }
    }

    @IBOutlet weak var errorMessageLabel: UILabel! {
        didSet {
            errorMessageLabel.font = .BodySmall()
            errorMessageLabel.text = PILocalizedString("ciolSpecialOccasionErrorMessage")
        }
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)

        setup()
    }

    private func setup() {
        selectionStyle = .none
    }

    @IBAction func toggle(_ sender: UISwitch) {
        selectButton.isHidden = !occasionSwitch.isOn
        didToggleSpecialOccasion?(occasionSwitch.isOn)
    }
}
