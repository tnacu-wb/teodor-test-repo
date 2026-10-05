//
//  ChildrenStepperCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/06/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

class ChildrenStepperCell: ChildrenSelectorCell {
    @IBOutlet weak var primaryText: UILabel! {
        didSet {
            let string = PILocalizedString("childrenRoomStepper", comment: "")
            let mutableString = NSMutableAttributedString(
                string: string,
                attributes: [.foregroundColor: UIColor.TintD1, .font: UIFont.Heading4_Semibold()]
            )
            let yearsOld = PILocalizedString("childrenYearsRange")
            let mutableYearsOld = NSMutableAttributedString(
                string: yearsOld,
                attributes: [.foregroundColor: UIColor.TintD1, .font: UIFont.Body()]
            )

            mutableString.append(mutableYearsOld)
            primaryText.attributedText = NSAttributedString(attributedString: mutableString)
        }
    }
    @IBOutlet weak var detail: UILabel! {
        didSet {
            detail.font = UIFont.BodySmall()
            detail.text = PILocalizedString("maxPersonRoom")
        }
    }
}

extension ChildrenStepperCell: CellWithRuleErrorColorToggleCell {
    var ruleLabel: UILabel? {
        detail
    }
}
