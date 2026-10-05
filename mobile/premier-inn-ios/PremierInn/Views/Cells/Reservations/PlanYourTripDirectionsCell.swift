//
//  PlanYourTripDirectionsCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 17/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class PlanYourTripDirectionsCell: UITableViewCell {
    @IBOutlet weak var directionsLabel: UILabel! {
        didSet {
            directionsLabel.font = UIFont.Body_Semibold()
            directionsLabel.text = PILocalizedString(
                "hotelDetailsDirectionsButtonTitle",
                comment: "Hotel details: directions button title"
            )
            directionsLabel.textColor = .BasePurple
        }
    }
    @IBOutlet weak var directions: UILabel! {
        didSet {
            directions.font = UIFont.Body()
        }
    }
}
