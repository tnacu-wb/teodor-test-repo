//
//  BookingSummaryCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 23/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class BookingSummaryCell: UITableViewCell {
    @IBOutlet weak var priceViewHeightConstraint: NSLayoutConstraint!
    @IBOutlet weak var dottedLine: DottedLine!
    @IBOutlet var hotelImage: UIImageView!
    @IBOutlet var hotelName: UILabel! {
        didSet {
            hotelName.text = nil
            hotelName.textColor = .TintD1
            hotelName.font = UIFont.Body_Semibold()
        }
    }
    @IBOutlet var stayDetails: UILabel! {
        didSet {
            stayDetails.text = nil
            stayDetails.textColor = .TintD1
            stayDetails.font = UIFont.BodySmall()
        }
    }
    @IBOutlet var totalCost: UILabel! {
        didSet {
            totalCost.text = nil
            totalCost.textColor = .TintD1
            totalCost.font = UIFont.Heading2_Semibold()
        }
    }
    @IBOutlet var costLabel: UILabel! {
        didSet {
            costLabel.text = PILocalizedString("bookingSummaryTotalLabel", comment: "Booking summary total label")
            costLabel.textColor = .TintD1
            costLabel.font = UIFont.Body_Semibold()
        }
    }
    @IBOutlet weak var paymentBreakdownLabel: UILabel! {
        didSet {
            paymentBreakdownLabel.text = nil
            paymentBreakdownLabel.textColor = UIColor.BasePurple
            paymentBreakdownLabel.font = UIFont.BodySmall()
        }
    }
}
