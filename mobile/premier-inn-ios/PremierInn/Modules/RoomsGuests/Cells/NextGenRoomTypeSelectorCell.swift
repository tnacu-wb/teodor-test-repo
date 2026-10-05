//
//  NextGenRoomTypeSelectorCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/06/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

class NextGenRoomTypeSelectorCell: RoomTypeSelectorCell {
    @IBOutlet weak var inputLabel: UIPaddingLabel! {
        didSet {
            inputLabel.font = UIFont.Body()
            inputLabel.text = PILocalizedString("Room type")
        }
    }
}
