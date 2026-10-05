//
//  BookingConfirmationEventCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 16/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Formeka

class BookingConfirmationViewPassCell: SimpleSeparatorsCell {
    @IBOutlet weak var viewPassButton: RoundedCornersTintButton! {
        didSet {
            viewPassButton.titleLabel?.font = .Button1()
        }
    }
}
