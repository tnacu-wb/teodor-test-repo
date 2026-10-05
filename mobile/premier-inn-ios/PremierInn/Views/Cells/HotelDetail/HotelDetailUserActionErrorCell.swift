//
//  HotelDetailUserActionErrorCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 22/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class HotelDetailUserActionErrorCell: UITableViewCell {
    var didTapCtaButton2: (() -> Void)?

    @IBOutlet weak var ctaButton1: RoundedCornersButton! {
        didSet {
            ctaButton1.setTitleColor(.BaseWhite, for: .normal)
            ctaButton1.backgroundColor = .Tint1
            ctaButton1.titleLabel?.font = .Button1()
        }
    }
    @IBOutlet weak var ctaButton2: UIButton! {
        didSet {
            ctaButton2.setTitleColor(.TintD1, for: .normal)
            ctaButton2.setTitle(
                PILocalizedString(
                    "hotelDetailsFullyBookedEditDates",
                    comment: "Hotel details: edit dates button title"
                ),
                for: .normal
            )
            ctaButton2.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.showMeNearByHotelsButton
            ctaButton2.titleLabel?.font = .BodySmall()
            ctaButton2.layer.borderColor = UIColor.TintL4.cgColor
            ctaButton2.layer.borderWidth = 1
            ctaButton2.layer.cornerRadius = 4
        }
    }
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView! {
        didSet {
            activityIndicator.color = .BaseWhite
        }
    }

    @IBAction func changeDate(_ sender: Any) {
        didTapCtaButton2?()
    }
}
