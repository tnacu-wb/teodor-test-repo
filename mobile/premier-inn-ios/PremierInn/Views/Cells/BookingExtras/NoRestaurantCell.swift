//
//  NoRestaurantCell.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 09/05/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class NoRestaurantCell: UITableViewCell {
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
