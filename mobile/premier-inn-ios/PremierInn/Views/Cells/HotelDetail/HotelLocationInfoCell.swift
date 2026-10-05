//
//  HotelLocationInfoCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class HotelLocationInfoCell: UITableViewCell {
    @IBOutlet weak var subtitle: UILabel?
    @IBOutlet weak var readMoreButton: RoundedCornersButton! {
        didSet {
            readMoreButton.setTitle(
                PILocalizedString("hotelDetailsReadMoreButtonTitle", comment: "Hotel details: read more button title"),
                for: .normal
            )
            readMoreButton.titleLabel?.textColor = UIColor.BasePurple
            readMoreButton.titleLabel?.font = .Heading4_Medium()
            readMoreButton.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.hotelDetailsReadMoreButton
            readMoreButton.layer.borderColor = UIColor.clear.cgColor
            readMoreButton.layer.borderWidth = 0
        }
    }
    @IBOutlet weak var directionsButton: RoundedCornersButton! {
        didSet {
            directionsButton.setTitle(
                PILocalizedString("hotelDetailsDirectionsButtonTitle", comment: "Hotel details: directions button title"),
                for: .normal
            )
            directionsButton.titleLabel?.textColor = UIColor.BasePurple
        }
    }
}
