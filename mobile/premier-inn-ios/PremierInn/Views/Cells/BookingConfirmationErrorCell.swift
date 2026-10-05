//
//  BookingConfirmationInfoCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 23/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class BookingConfirmationErrorCell: UITableViewCell {
    @IBOutlet weak var message: UILabel! {
        didSet {
            message.accessibilityIdentifier = "bookingConfirmationMessage"
            message.font = .Body()
            message.textColor = .white
            message.text = nil
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()

        contentView.backgroundColor = UIColor.sea
    }
}
