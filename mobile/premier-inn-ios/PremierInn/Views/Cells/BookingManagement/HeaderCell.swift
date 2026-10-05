//
//  HeaderCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class HeaderCell: SimpleSeparatorsCell {
    @IBOutlet weak var headerTitleLabel: UILabel! {
        didSet {
            headerTitleLabel.text = nil
            headerTitleLabel.font = UIFont.Heading3_Semibold()
            headerTitleLabel.textColor = UIColor.BasePurple
        }
    }
}

class FooterCell: UITableViewCell {
}
