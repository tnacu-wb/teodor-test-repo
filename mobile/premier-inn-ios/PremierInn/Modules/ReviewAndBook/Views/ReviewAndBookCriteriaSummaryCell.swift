//
//  ReviewAndBookCriteriaSummaryCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 27/09/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class ReviewAndBookCriteriaSummaryCell: UITableViewCell {
    @IBOutlet weak var criteriaSummaryLabel: UILabel! {
        didSet {
            criteriaSummaryLabel.text = nil
            criteriaSummaryLabel.textColor = .black
            criteriaSummaryLabel.backgroundColor = .clear
        }
    }

    @IBOutlet weak var editButton: UIButton! {
        didSet {
            editButton.setTitleColor(.grape, for: .normal)
            editButton.setTitle(
                PILocalizedString("actionableEditButtonTitle", comment: "Actionable Header: edit button title"),
                for: .normal
            )
            editButton.titleLabel?.font = UIFont.Action1()
        }
    }
}
