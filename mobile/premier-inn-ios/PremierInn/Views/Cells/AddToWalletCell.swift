//
//  AddToWalletCell.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 14/06/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka
import PassKit

class AddToWalletCell: SimpleSeparatorsCell {
    @IBOutlet weak var addToWalletButton: PKAddPassButton!

    @IBOutlet weak var addToWalletLabel: UILabel! {
        didSet {
            self.addToWalletLabel.font = .Heading2_Bold()
            self.addToWalletLabel.textColor = .BasePurple
        }
    }
}
