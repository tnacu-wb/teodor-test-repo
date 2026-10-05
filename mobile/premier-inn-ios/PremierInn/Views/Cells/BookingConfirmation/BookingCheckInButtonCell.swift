//
//  BookingCheckInButtonCell.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 01.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class BookingCheckInButtonCell: SimpleSeparatorsCell {
    var buttonTitle: String? {
        didSet {
            checkInButton.setTitle(
                buttonTitle,
                for: .normal
            )
        }
    }

    @IBOutlet weak var checkInButton: UIButton! {
        didSet {
            checkInButton.accessibilityIdentifier = AccessibilityIdentifiers.CIOL.ciolCheckIntButton
            checkInButton.setTitle(
                PILocalizedString("ciolCheckInOnlineTitleButton"),
                for: .normal
            )
            checkInButton.setTitleColor(.white, for: .normal)
            checkInButton.titleLabel?.font = .Button1()
            checkInButton.tintColor = .white
            checkInButton.backgroundColor = .Tint1
            checkInButton.layer.cornerRadius = 4
        }
    }
}
