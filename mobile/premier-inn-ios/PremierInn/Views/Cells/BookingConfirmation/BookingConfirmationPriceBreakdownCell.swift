//
//  BookingConfirmationPriceBreakdownCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/12/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class BookingConfirmationPriceBreakdownCell: DisclosureCell {
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .Action3()
            titleLabel.textColor = .BasePurple
            titleLabel.text = PILocalizedString(
                "bookingConfirmationPriceBreakDown",
                comment: "Title for price breakdown cell"
            )
        }
    }
}
