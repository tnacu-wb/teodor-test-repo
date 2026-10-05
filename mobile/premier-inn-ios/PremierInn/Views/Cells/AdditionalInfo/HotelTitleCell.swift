//
//  HotelTitleCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 26/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class HotelTitleCell: UITableViewCell {
    @IBOutlet weak var hotelName: UILabel! {
        didSet {
            hotelName.setupLabel(
                font: .Heading2_Bold(),
                textColor: .TintD1,
                accessibilityIdentifier: "hotelName"
            )
        }
    }
}
