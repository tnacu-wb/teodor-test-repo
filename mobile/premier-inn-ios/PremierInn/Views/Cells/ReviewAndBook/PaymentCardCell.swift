//
//  PaymentCardCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 28/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class PaymentCardCell: SimpleSeparatorsCell {
    @IBOutlet weak var cardName: UILabel! {
        didSet {
            cardName.font = .Heading3_Semibold()
            cardName.textColor = UIColor.slateGrey
            cardName.text = nil
        }
    }
    @IBOutlet weak var fee: UILabel! {
        didSet {
            fee.font = .BodySmall()
            fee.textColor = UIColor.slateGrey
            fee.text = nil
        }
    }
    @IBOutlet weak var lastFourDigits: UILabel! {
        didSet {
            lastFourDigits.font = .Body()
            lastFourDigits.textColor = UIColor.TintD1
            lastFourDigits.text = nil
        }
    }

    @IBOutlet weak var storedBusinessOrPersonalLabel: UILabel! {
        didSet {
            storedBusinessOrPersonalLabel.font = .Body()
            storedBusinessOrPersonalLabel.textColor = UIColor.TintD1
            storedBusinessOrPersonalLabel.text = nil
        }
    }

    @IBOutlet weak var icon: UIImageView! {
        didSet {
            icon.image = nil
        }
    }
    @IBOutlet weak var cardView: UIView! {
        didSet {
            cardView.layer.borderColor = UIColor.warmGrey.cgColor
            cardView.layer.borderWidth = 1
            cardView.layer.cornerRadius = 4
        }
    }
}
