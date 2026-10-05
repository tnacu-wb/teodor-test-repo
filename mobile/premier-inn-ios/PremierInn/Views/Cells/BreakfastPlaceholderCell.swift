//
//  BreakfastPlaceholderCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 11/10/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class BreakfastPlaceholderCell: UITableViewCell {
    @IBOutlet weak var primaryDescription: UILabel!
    @IBOutlet weak var secondaryDescription: UILabel!
}

extension BreakfastPlaceholderCell: BookingReviewCellProtocol {
    func configure(withBookingDetails bookingDetails: BookingDetails, withIndexPath indexPath: IndexPath, delegate: Any) {
        primaryDescription.text = PILocalizedString(
            "Soon you’ll be able to add breakfast when you book",
            comment: "Break primary string"
        )
        secondaryDescription.attributedText = NSAttributedString.attributedStringWith(
            PILocalizedString(
                "In the meantime, you can order our delicious breakfasts at your hotel during your stay",
                comment: "Break second string"
            ),
            lineSpacing: 5,
            font: secondaryDescription.font,
            textColor: secondaryDescription.textColor,
            textAlignment: secondaryDescription.textAlignment
        )
    }
}
