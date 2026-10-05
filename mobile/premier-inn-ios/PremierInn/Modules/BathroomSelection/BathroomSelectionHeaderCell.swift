//
//  BathroomSelectionHeader.swift
//  PremierInn
//
//  Created by Nick Jones on 08/08/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class BathroomSelectionHeaderCell: UITableViewCell {
    @IBOutlet weak var roomNumberLabel: UILabel! {
        didSet {
            roomNumberLabel.font = .Heading2_Semibold()
            roomNumberLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var roomCriteriaLabel: UILabel! {
        didSet {
            roomCriteriaLabel.font = .Body()
            roomCriteriaLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var alternativeRoomLabel: UILabel! {
        didSet {
            alternativeRoomLabel.font = .SubtextSmall()
            alternativeRoomLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var alternativeRoomBackground: UIView! {
        didSet {
            alternativeRoomBackground.clipsToBounds = true
            alternativeRoomBackground.layer.cornerRadius = 2
            alternativeRoomBackground.backgroundColor = .Tint10
        }
    }
    @IBOutlet weak var changeRoomButton: UIButton! {
        didSet {
            changeRoomButton.titleLabel?.font = .Action1()
            changeRoomButton.setTitleColor(.BasePurple, for: .normal)
        }
    }
    @IBOutlet weak var accessibleIcon: UIImageView! {
        didSet {
            accessibleIcon.image = UIImage(named: "iconDisabled")
        }
    }
    @IBOutlet weak var roomTypeToIconConstraint: NSLayoutConstraint!

    @IBOutlet weak var roomTypeLabel: UILabel! {
        didSet {
            roomTypeLabel.font = .Body()
            roomTypeLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var solidSeparator: UIView!
}
