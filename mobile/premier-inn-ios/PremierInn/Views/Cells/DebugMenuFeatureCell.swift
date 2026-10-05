//
//  DebugMenuFeatureCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 06/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

protocol DebugMenuFeatureCellDelegate: AnyObject {
    func featureCellActiveSwitchDidToggle(_ sender: DebugMenuFeatureCell, active: Bool)
}

class DebugMenuFeatureCell: UITableViewCell {
    @IBOutlet weak var titleLabel: UILabel!
	@IBOutlet weak var subtitleLabel: UILabel!
    @IBOutlet weak var activeSwitch: UISwitch!

	weak var delegate: DebugMenuFeatureCellDelegate?

    @IBAction func activeSwitchValueChanged(_ sender: AnyObject) {
        delegate?.featureCellActiveSwitchDidToggle(self, active: activeSwitch.isOn)
    }
}
