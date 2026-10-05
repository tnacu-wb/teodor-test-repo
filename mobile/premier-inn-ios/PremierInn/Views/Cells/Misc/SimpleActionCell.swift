//
//  SimpleActionCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 22/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class SimpleActionCell: DisclosureCell {
    // MARK: - DisclosureCell

    override var textLabel: UILabel? {
        self.action
    }

    // MARK: - Views

    @IBOutlet weak var leadingConstraint: NSLayoutConstraint!
    @IBOutlet weak var action: UILabel! {
        didSet {
            action.font = UIFont.Body()
            action.isAccessibilityElement = true
        }
    }
}
