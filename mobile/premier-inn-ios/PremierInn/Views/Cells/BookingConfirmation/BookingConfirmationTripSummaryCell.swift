//
//  BookingConfirmationTripSummaryCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 16/12/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class BookingConfirmationTripSummaryCell: SimpleSeparatorsCell {
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.textColor = .TintD1
            titleLabel.font = .Heading4_Semibold()
            titleLabel.text = nil
        }
    }
    @IBOutlet weak var guestsRoomsSummary: UILabel! {
        didSet {
            guestsRoomsSummary.accessibilityIdentifier = "guestsRoomsSummaryLabelAcc"
            guestsRoomsSummary.font = .Body()
        }
    }
}
