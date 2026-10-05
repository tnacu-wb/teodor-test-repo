//
//  BookingReviewTotalPriceCell.swift
//  PremierInn
//
//  Created by Simon Antoine on 31/03/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

class BookingReviewTotalPriceCell: UITableViewCell {
    @IBOutlet weak var totalLabel: UILabel! {
        didSet {
            totalLabel.text = PILocalizedString("bookingReviewTotalCellLabel", comment: "Booking review total cell label")
            totalLabel.font = .Heading2_Semibold()
            totalLabel.textColor = UIColor.TintD1
        }
    }
    @IBOutlet weak var totalPriceLabel: UILabel! {
        didSet {
            totalPriceLabel.text = nil
            totalPriceLabel.font = .Heading1_Semibold()
            totalPriceLabel.textColor = UIColor.TintD1
        }
    }
    @IBOutlet weak var cityTaxLabel: UILabel! {
        didSet {
            cityTaxLabel.font = .SubtextSmall()
        }
    }
}
