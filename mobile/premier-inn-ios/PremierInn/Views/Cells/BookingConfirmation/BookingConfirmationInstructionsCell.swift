//
//  BookingConfirmationInstructionsCell.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 22.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class BookingConfirmationInstructionsCell: DisclosureCell {
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .Action3()
            titleLabel.textColor = .BasePurple
            titleLabel.text = PILocalizedString(
                PILocalizedString("ciolGetRoomKey"),
                comment: "Booking confirmation instructions section label"
            )
        }
    }
}
