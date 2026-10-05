//
//  PlanYourTripParkingCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 17/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class PlanYourTripParkingCell: UITableViewCell {
    @IBOutlet weak var parkingLabel: UILabel! {
        didSet {
            parkingLabel.font = UIFont.Body_Semibold()
            parkingLabel.text = PILocalizedString(
                "hotelDetailsParkingSectionTitle",
                comment: "Hotel details: parking section title"
            )
            parkingLabel.textColor = .BasePurple
        }
    }
    @IBOutlet weak var parkingInfo: UILabel! {
           didSet {
               parkingInfo.font = UIFont.Body()
           }
       }
}
