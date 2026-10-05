//
//  PreStayAddressCell.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 06.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class PreStayAddressCell: SimpleSeparatorsCell {
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .Body_Medium()
            titleLabel.textColor = .BaseBlack
            titleLabel.textAlignment = .left
        }
    }

    @IBOutlet weak var valueLabel: UILabel! {
        didSet {
            valueLabel.font = .Body()
            valueLabel.textColor = .TintD1
            valueLabel.textAlignment = .right
        }
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)

        setup()
    }

    private func setup() {
        selectionStyle = .none
    }
}
