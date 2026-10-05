//
//  FormekaToggleButtonCell.swift
//  PremierInn
//
//  Created by Simon Antoine on 02/03/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

public protocol FormekaToggleButtonCellDelegate: AnyObject {
    func toggleButtonDidTap(cell: FormekaToggleButtonCell, isOn: Bool)
}

public class FormekaToggleButtonCell: UITableViewCell {
    public weak var delegate: FormekaToggleButtonCellDelegate?

    @IBOutlet public weak var button: UISwitch!
    @IBOutlet public weak var label: UILabel!

    @IBAction func switchButtonDidTap(_ sender: UISwitch) {
        delegate?.toggleButtonDidTap(cell: self, isOn: button.isOn)
    }

    func cellIsDisable(_ isOn: Bool) {
        button.isEnabled = isOn
        label.isEnabled = isOn

        if button.isEnabled == false {
            button.isOn = false
            switchButtonDidTap(button)
        }
    }
}
