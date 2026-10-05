//
//  AdultsStepperCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/06/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

class AdultsStepperCell: AdultsSelectorCell {
    @IBOutlet weak var adultsLabel: UILabel! {
        didSet {
            adultsLabel.font = UIFont.Heading4_Semibold()
            adultsLabel.text = PILocalizedString("adultTitleRoomStepper")
        }
    }
    @IBOutlet weak var detail: UILabel! {
        didSet {
            detail.font = UIFont.BodySmall()
            detail.text = PILocalizedString("maxPersonRoom")
        }
    }
}

extension AdultsStepperCell: CellWithRuleErrorColorToggleCell {
    var ruleLabel: UILabel? {
        detail
    }
}
