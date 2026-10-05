//
//  BookingReviewAdditionsCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 03/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class BookingReviewAdditionsCell: SimpleSeparatorsCell {
    @IBOutlet weak var titleLabelTopConstraint: NSLayoutConstraint!
    @IBOutlet weak var valueBottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.text = nil
            titleLabel.font = UIFont.Body()
            titleLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var valueLabel: UILabel! {
        didSet {
            valueLabel.text = nil
            valueLabel.font = UIFont.Heading2_Semibold()
            valueLabel.textColor = .TintD1
        }
    }
}
