//
//  BookingCheckInButtonListCell.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 01.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class BookingCheckInButtonListCell: BorderedContentViewCell {
    @IBOutlet weak var checkInButton: UIButton! {
        didSet {
            checkInButton.accessibilityIdentifier = AccessibilityIdentifiers.CIOL.ciolCheckIntButton
            checkInButton.setTitle(
                PILocalizedString("ciolCheckInOnlineTitleButton"),
                for: .normal
            )
            checkInButton.setTitleColor(.white, for: .normal)
            checkInButton.titleLabel?.font = .Heading3_Semibold()
            checkInButton.tintColor = .white
            checkInButton.backgroundColor = .Tint1
            checkInButton.layer.cornerRadius = 4
        }
    }
}
