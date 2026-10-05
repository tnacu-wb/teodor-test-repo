//
//  BookingReviewTotalCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 13/10/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class BookingReviewSubmitCell: UITableViewCell {
    @IBOutlet weak var continueButton: FadeOnHighlightButton! {
        didSet {
            continueButton.setTitle(
                PILocalizedString("bookingReviewContinueButtonTitle", comment: "Booking review continue button title"),
                for: .normal
            )
            continueButton.setTitleColor(UIColor.BaseWhite, for: .normal)
            continueButton.titleLabel?.font = .Heading3_Semibold()
            continueButton.backgroundColor = UIColor.Tint1
            continueButton.setTitle(PILocalizedString("Continue"), for: .normal)
        }
    }
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView! {
        didSet {
            activityIndicator.color = .BaseWhite
        }
    }
}
