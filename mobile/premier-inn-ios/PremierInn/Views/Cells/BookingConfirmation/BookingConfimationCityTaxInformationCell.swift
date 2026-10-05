//
//  BookingConfirmationCityTaxInformationCell.swift
//  PremierInn
//
//  Created by Nick Jones on 23/08/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

class BookingConfirmationCityTaxInformationCell: UITableViewCell {
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.text = PILocalizedString(
                "bookingConfirmationCityTaxTitle",
                comment: "Booking confirmation city tax title"
            )
            titleLabel.font = .Heading2_Semibold()
            titleLabel.textColor = UIColor.TintD1
        }
    }

    @IBOutlet weak var bodyLabel: UILabel! {
        didSet {
            bodyLabel.text = PILocalizedString(
                "bookingConfirmationCityTaxDescription",
                comment: "Booking confirmation city tax description"
            )
            bodyLabel.font = .Body()
            bodyLabel.textColor = UIColor.TintD1
        }
    }

    @IBOutlet weak var viewFormButton: RoundedCornersTintButton! {
        didSet {
            viewFormButton.setTitle(
                PILocalizedString(
                    "bookingConfirmationCityTaxViewFormButtonTitle",
                    comment: "Booking confirmation city tax view form button title"
                ),
                for: .normal
            )
            viewFormButton.tintColor = UIColor.grape
            viewFormButton.setTitleColor(UIColor.grape, for: .normal)
            viewFormButton.titleLabel?.font = .Button1()
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()

        backgroundColor = .whiteTwo
    }
}
