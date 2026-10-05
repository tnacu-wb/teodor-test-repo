//
//  BookingConfirmationTotalPriceCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/12/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class BookingConfirmationTotalPriceCell: UITableViewCell {
    @IBOutlet weak var totalLabel: UILabel! {
        didSet {
            totalLabel.font = .Heading2_Semibold()
            totalLabel.textColor = UIColor.TintD1
            totalLabel.text = PILocalizedString(
                "bookingConfirmationTotalPriceTitle",
                comment: "Booking confirmation total price title"
            )
        }
    }
    @IBOutlet weak var totalPrice: UILabel! {
        didSet {
            totalPrice.accessibilityIdentifier = "totalPriceLabelAcc"
            totalPrice.font = .Heading1_Semibold()
            totalPrice.textColor = UIColor.TintD1
        }
    }
    @IBOutlet weak var rateLabel: UILabel! {
        didSet {
            rateLabel.font = .BodySmall()
            rateLabel.textColor = UIColor.TintD1
            rateLabel.text = PILocalizedString("bookingConfirmationRateLabel", comment: "Booking confirmation rate label")
        }
    }
    @IBOutlet weak var rate: UILabel! {
        didSet {
            rate.accessibilityIdentifier = "rateLabelAcc"
            rate.font = .Body()
            rate.textColor = UIColor.Tint1
        }
    }

    @IBOutlet weak var outstandingAmount: UILabel! {
        didSet {
            outstandingAmount.accessibilityIdentifier = "balanceOutstandingLabelAcc"
            outstandingAmount.font = .Heading1_Semibold()
            outstandingAmount.textColor = UIColor.TintD1
        }
    }

    @IBOutlet weak var outstandingAmountLabel: UILabel! {
        didSet {
            outstandingAmountLabel.font = .Heading2_Semibold()
            outstandingAmountLabel.textColor = UIColor.TintD1
            outstandingAmountLabel.text = PILocalizedString(
                "bookingConfirmationBalanceOutstandingTitle",
                comment: "Booking confirmation total price title"
            )
        }
    }
}
