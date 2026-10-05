//
//  BookingSummaryCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 23/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class BookingSummaryHeaderCell: UITableViewCell {
    @IBOutlet var hotelImage: UIImageView!
    @IBOutlet var stayDetails: UILabel! {
        didSet {
            stayDetails.text = nil
            stayDetails.textColor = .TintD1
            stayDetails.font = UIFont.BodySmall()
        }
    }
    @IBOutlet var hotelName: UILabel! {
        didSet {
            hotelName.text = nil
            hotelName.textColor = .TintD1
            hotelName.font = UIFont.Heading4_Semibold()
        }
    }
}
