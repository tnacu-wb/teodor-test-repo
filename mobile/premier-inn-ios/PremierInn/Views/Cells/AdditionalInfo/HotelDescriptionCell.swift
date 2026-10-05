//
//  HotelDescriptionCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 26/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class HotelDescriptionCell: UITableViewCell {
    @IBOutlet weak var hotelDescription: UILabel! {
        didSet {
             hotelDescription.font = UIFont.Body()
        }
    }
}
