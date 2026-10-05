//
//  ArrivalDateSelectorCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 01/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class ArrivalDateSelectorCell: UITableViewCell {
    @IBOutlet private weak var calendarButton: SeparatedStepperButton!
    @IBOutlet weak var arrivalDateLabel: UILabel! {
        didSet {
            arrivalDateLabel.accessibilityIdentifier = "arrivalDateAcc"
            arrivalDateLabel.font = .Heading2_Regular()
        }
    }

    override var accessibilityHint: String? {
        get {
            PILocalizedString("Double-tap to change arrival date.", comment: "")
        }
        set {
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()

        calendarButton.isEnabled = true
        arrivalDateLabel.text = Date.localizedMediumDateStringForToday
    }
}
