//
//  DropdownActivityCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 08/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class DropdownActivityCell: UITableViewCell {
    @IBOutlet weak var activityTitleLabel: UILabel! {
        didSet {
            activityTitleLabel.font = .Heading2_Semibold()
        }
    }
    @IBOutlet weak var activitySubtitleLabel: UILabel! {
        didSet {
            activitySubtitleLabel.font = .Body()
        }
    }
    @IBOutlet weak var activityIcon: UIImageView!
    @IBOutlet weak var activityIconContainer: RoundedCornersView!

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        styleSelected(selected)
    }

    func styleSelected(_ selected: Bool) {
        if selected == true {
            (
                activityIconContainer.layer.borderWidth,
                activityIconContainer.backgroundColor,
                activityTitleLabel.font,
                activityIcon.tintColor
            ) = (0.0, .greyPurple, .Heading2_Semibold(), .white)
        } else {
            (
                activityIconContainer.layer.borderWidth,
                activityIconContainer.backgroundColor,
                activityTitleLabel.font,
                activityIcon.tintColor
            ) = (1.0, .white, .Heading2_Regular(), .BasePurple)
        }
    }
}
