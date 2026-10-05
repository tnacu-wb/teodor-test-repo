//
//  CotSelectorCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 22/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

protocol CotSelectorCellDelegate: AnyObject {
    func cotSwitchDidChange(cell: CotSelectorCell)
}

class CotSelectorCell: UITableViewCell {
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.text = PILocalizedString(
                "criteriaIncludeCotSwitchLabel",
                comment: "Criteria screen: include cot switch label"
            )
            titleLabel.font = UIFont.Heading4_Semibold()
        }
    }
    @IBOutlet weak var cotSelectorSwitch: UISwitch!
    weak var delegate: CotSelectorCellDelegate?

    @IBAction func cotSelectorSwitchValueDidChange(_ sender: AnyObject) {
        delegate?.cotSwitchDidChange(cell: self)
    }
}
