//
//  BookingConfirmationHotelInfoCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 09/12/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class BookingConfirmationHotelInfoCell: SimpleSeparatorsCell {
    @IBOutlet weak var hotelImageView: UIImageView! {
        didSet {
            hotelImageView.layer.cornerRadius = 8
        }
    }

    @IBOutlet var hotelNameLabel: UILabel! {
        didSet {
            hotelNameLabel.accessibilityIdentifier = "hotelNameLabelAcc"
            hotelNameLabel.font = .Heading1_ExtraBold()
            hotelNameLabel.textColor = UIColor.BasePurple
        }
    }
}
