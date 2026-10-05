//
//  ManagingBookingActionCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 22/12/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class ManagingBookingActionCell: SubtitleDisclosureCell {
    override var textLabel: UILabel? {
        self.action
    }

    @IBOutlet weak var action: UILabel! {
        didSet {
            action.font = .Action3()
            action.textColor = .BasePurple
        }
    }
}
