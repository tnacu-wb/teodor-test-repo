//
//  SummaryTotalCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 12/10/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class SummaryTotalCell: UITableViewCell {
    @IBOutlet weak var lineView: UIView! {
        didSet {
            lineView.backgroundColor = .TintL2
        }
    }
	@IBOutlet weak var descriptionLabel: UILabel! {
		didSet {
			descriptionLabel.textColor = .TintD1
            descriptionLabel.text = nil
            descriptionLabel.font = .Heading2_Semibold()
		}
	}

	@IBOutlet weak var priceLabel: UILabel! {
		didSet {
			priceLabel.textColor = .TintD1
            priceLabel.text = nil
            priceLabel.font = .Heading1_Semibold()
		}
	}

    @IBOutlet weak var rateLabel: UILabel! {
        didSet {
            rateLabel.accessibilityIdentifier = "summaryRateLabelAcc"
            rateLabel.textColor = .sea
            rateLabel.text = nil
            rateLabel.font = .Body()
        }
    }

    @IBOutlet weak var cityTaxLabel: UILabel! {
        didSet {
            cityTaxLabel.font = .SubtextSmall()
            cityTaxLabel.textColor = .TintD1
        }
    }
}
