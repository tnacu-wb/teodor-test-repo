//
//  NoMealsCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class NoMealsCell: UITableViewCell {
    @IBOutlet weak var radioButton: RadioButtonView!
    @IBOutlet weak var bottomView: UIView! {
        didSet {
            bottomView.backgroundColor = .whiteTwo
        }
    }
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .Heading4_Semibold()
        }
    }
    @IBOutlet weak var descriptionLabel: UILabel! {
        didSet {
            descriptionLabel.font = .BodySmall()
        }
    }
}
