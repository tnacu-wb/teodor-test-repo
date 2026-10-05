//
//  SummaryDatesCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 12/10/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class SummaryDatesCell: UITableViewCell {
	@IBOutlet weak var titleLabel: UILabel! {
		didSet {
			titleLabel.textColor = UIColor.Tint1
            titleLabel.text = nil
            titleLabel.font = .Heading3_Semibold()
		}
	}
	@IBOutlet weak var contentLabel: UILabel! {
		didSet {
			contentLabel.textColor = UIColor.TintD1
            contentLabel.text = nil
            contentLabel.font = .Body()
		}
	}
}
