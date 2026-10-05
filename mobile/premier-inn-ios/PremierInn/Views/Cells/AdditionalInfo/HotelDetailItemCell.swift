//
//  HotelDetailItemCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 26/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class HotelDetailItemCell: UITableViewCell {
    @IBOutlet weak var hotelDetailTitle: UILabel! {
        didSet {
            hotelDetailTitle.setupLabel(
                font: .Heading3_Bold(),
                textColor: .TintD1,
                accessibilityIdentifier: "hotelDetailTitle"
            )
        }
    }
    @IBOutlet weak var hotelDetailDescription: UILabel! {
           didSet {
             hotelDetailDescription.font = UIFont.Body()
           }
       }
}
