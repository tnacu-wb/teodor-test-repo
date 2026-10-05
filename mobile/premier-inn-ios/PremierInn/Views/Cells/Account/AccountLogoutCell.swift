//
//  AccountLogoutCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 22/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class AccountLogoutCell: UITableViewCell {
    @IBOutlet weak var username: UILabel! {
        didSet {
            username.font = UIFont.Heading1_Bold()
            username.textColor = .BasePurple
            username.isAccessibilityElement = true
        }
    }
    @IBOutlet weak var email: UILabel! {
        didSet {
            email.font = UIFont.Body_Medium()
            email.textColor = .ColourDL1
            email.isAccessibilityElement = true
        }
    }
}
