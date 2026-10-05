//
//  BookingConfirmationInfoCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 23/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class BookingConfirmationInfoCell: UITableViewCell {
    @IBOutlet weak var heading: UILabel! {
        didSet {
            heading.font = .Heading1_ExtraBold()
            heading.textColor = .Tint1
            heading.text = nil
        }
    }
    @IBOutlet weak var message: UILabel! {
        didSet {
            message.accessibilityIdentifier = "bookingConfirmationMessage"
            message.font = .Body()
            message.textColor = .ColourDL1
            message.text = nil
        }
    }
}
