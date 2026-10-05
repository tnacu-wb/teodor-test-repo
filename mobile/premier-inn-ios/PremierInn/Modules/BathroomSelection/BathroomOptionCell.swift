//
//  BathroomOptionCell.swift
//  PremierInn
//
//  Created by Nick Jones on 04/07/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class BathroomOptionCell: UITableViewCell {
    @IBOutlet weak var radioButton: RadioButtonView!
    @IBOutlet weak var bathroomTitle: UILabel! {
        didSet {
            bathroomTitle.font = .Heading4_Semibold()
        }
    }
    @IBOutlet weak var priceLabel: UILabel! {
        didSet {
            priceLabel.font = .Heading4_Semibold()
        }
    }
    @IBOutlet weak var bathroomDescription: UILabel! {
        didSet {
            bathroomDescription.font = .BodySmall()
        }
    }
}
