//
//  FAQActionCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 03/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class FAQActionCell: ManagingBookingActionCell {
    @IBOutlet weak var actionFAQ: UILabel! {
        didSet {
            actionFAQ.font = .Action3()
            actionFAQ.textColor = .BasePurple
            actionFAQ.text = PILocalizedString("bookingConfirmationFaqTitle")
        }
    }
}
