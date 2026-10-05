//
//  CostWithPayButtonCell.swift
//  PremierInn
//
//  Created by Santa Gurung on 27/03/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit

class CostWithPayButtonCell: UITableViewCell {
    @IBOutlet weak var totalCostLabel: UILabel! {
        didSet {
            totalCostLabel.textColor = .ColourDL1
            totalCostLabel.font = .Heading2_Bold()
        }
    }
    @IBOutlet weak var payLabel: UILabel! {
        didSet {
            payLabel.textColor = .ColourDL1
            payLabel.font = .Body_Medium()
        }
    }
    @IBOutlet weak var continueButton: FadeOnHighlightButton!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView! {
        didSet {
            activityIndicator.color = .BaseWhite
        }
    }
}
