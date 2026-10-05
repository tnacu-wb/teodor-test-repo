//
//  MealsInformationCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 27/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class MealsInformationCell: UITableViewCell {
    @IBOutlet weak var containerView: UIView! {
        didSet {
            containerView.layer.cornerRadius = 8
            containerView.clipsToBounds = true
        }
    }

    @IBOutlet weak var informationLabel: UILabel! {
        didSet {
            informationLabel.setupLabel(
                font: .Body(),
                lineHeightMultiple: 1.23,
                textAlignment: .left,
                textColor: .white,
                numberOfLines: 0,
                accessibilityIdentifier: "informationLabel"
            )
        }
    }

    @IBOutlet weak var saveLabel: UIPaddingLabel! {
        didSet {
            saveLabel.setupLabel(
                font: .SubtextSmall_Bold(),
                lineHeightMultiple: 1.23,
                textAlignment: .left,
                textColor: .BasePurple,
                numberOfLines: 1,
                accessibilityIdentifier: "saveLabel"
            )
            saveLabel.layer.cornerRadius = 4
            saveLabel.clipsToBounds = true
        }
    }
}
