//
//  BookingConfirmationParkingCell.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 11.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class BookingConfirmationParkingCell: DisclosureCell {
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .Action3()
            titleLabel.textColor = .BasePurple
            titleLabel.text = PILocalizedString(
                PILocalizedString("parkingAtThisHotel"),
                comment: "Booking confirmation parking section label"
            )
        }
    }
}
