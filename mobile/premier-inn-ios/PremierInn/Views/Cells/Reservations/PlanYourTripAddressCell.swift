//
//  PlanYourTripAddressCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 17/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class PlanYourTripAddressCell: UITableViewCell {
    @IBOutlet weak var distanceLabel: UILabel! {
        didSet {
            distanceLabel.font = UIFont.Body_Semibold()
        }
    }
    @IBOutlet weak var addressLabel: UILabel! {
           didSet {
            addressLabel.font = UIFont.Body()
           }
       }
}
