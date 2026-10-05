//
//  AccountTypeSwitchCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 10/07/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class LessSimpleSegmentedControl: SimpleSegmentedControl {
    override func setupControl() {
        super.setupControl()

        let image = #imageLiteral(resourceName: "LessSimpleSegmentedControlSelectedBackground")
        let dividerImage = #imageLiteral(resourceName: "trans").imageWithColor(UIColor.TintL2)

        setBackgroundImage(image, for: .normal, barMetrics: .default)
        setDividerImage(dividerImage, forLeftSegmentState: .normal, rightSegmentState: .normal, barMetrics: .default)
    }
}

protocol AccountTypeSwitchCellDelegate: AnyObject {
    func segmentedControlDidChange(cell: AccountTypeSwitchCell)
}

class AccountTypeSwitchCell: UITableViewCell {
    weak var delegate: AccountTypeSwitchCellDelegate?

    @IBOutlet weak var segmentedControl: LessSimpleSegmentedControl!

    @IBAction func segmentControlValueDidChange(_ sender: AnyObject) {
        delegate?.segmentedControlDidChange(cell: self)
    }
}
