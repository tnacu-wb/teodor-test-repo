//
//  RoomTypeSelectorCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class RoomTypeSelectorCell: UITableViewCell {
    @IBOutlet weak var roomTypeLabel: UILabel! {
        didSet {
            roomTypeLabel.font = UIFont.Body_Medium()
        }
    }
    @IBOutlet weak var roomTypeSelectorButton: SeparatedStepperButton!

    override func awakeFromNib() {
        super.awakeFromNib()

        roomTypeLabel.text = nil
        self.accessibilityTraits = .button
    }
}
