//
//  SummaryHotelCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 12/10/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class SummaryHotelCell: UITableViewCell {
	@IBOutlet weak var thumbView: UIImageView! {
		didSet {
			thumbView.layer.cornerRadius = 3
		}
	}

	@IBOutlet weak var nameLabel: UILabel! {
		didSet {
            nameLabel.accessibilityIdentifier = "summaryPageHotelNameAcc"
			nameLabel.textColor = UIColor.TintD1
            nameLabel.font = .Heading1_Semibold()
		}
	}

	@IBOutlet weak var addressLabel: UILabel! {
		didSet {
			addressLabel.textColor = UIColor.TintD1
            addressLabel.font = .Body()
		}
	}
}
