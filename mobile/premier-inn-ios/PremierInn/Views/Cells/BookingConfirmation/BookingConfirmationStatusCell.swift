//
//  BookingConfirmationStatusCell.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 16.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class BookingConfirmationStatusCell: SimpleSeparatorsCell {
    @IBOutlet weak var statusLabel: UILabel! {
        didSet {
            statusLabel.font = .SubtextSmall_Bold()
            statusLabel.layer.cornerRadius = 4
            statusLabel.clipsToBounds = true
        }
    }

    func configureCell(with bookingCheckStatus: BookingCheckStatus) {
        hiddenSeparatorLocations = [.bottom, .top]
        self.selectionStyle = .none
        switch bookingCheckStatus {
        case .checkIn:
            statusLabel.text = PILocalizedString("ciolCheckedInStatus")
            statusLabel.textColor = .Tint4
            statusLabel.backgroundColor = .Tint5
        case .checkOut:
            statusLabel.text = PILocalizedString("reservationsPastReservation")
            statusLabel.textColor = .TintD2
            statusLabel.backgroundColor = .TintL3
        }
    }
}
