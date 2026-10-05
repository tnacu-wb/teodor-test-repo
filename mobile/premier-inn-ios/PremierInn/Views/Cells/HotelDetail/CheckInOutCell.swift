//
//  CheckInOutCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/07/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class CheckInOutCell: SimpleSeparatorsCell {
    @IBOutlet weak var containerView: UIView! {
        didSet {
            containerView.layer.cornerRadius = 8
        }
    }
    @IBOutlet weak var checkinImageView: UIImageView!
    @IBOutlet weak var checkInLabel: UILabel! {
        didSet {
            checkInLabel.font = UIFont.Heading4_Bold()
            checkInLabel.textColor = .TintD1
            checkInLabel.text = PILocalizedString("checkInCellLabel", comment: "Check in cell label")
            checkInLabel.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.checkInTimeHeader
        }
    }
    @IBOutlet weak var checkoutImageView: UIImageView!
    @IBOutlet weak var checkOutLabel: UILabel! {
        didSet {
            checkOutLabel.font = UIFont.Heading4_Bold()
            checkOutLabel.textColor = .TintD1
            checkOutLabel.text = PILocalizedString("checkOutCellLabel", comment: "Check out cell label")
        }
    }
    @IBOutlet weak var checkInDateLabel: UILabel! {
        didSet {
            checkInDateLabel.font = UIFont.Body()
            checkInDateLabel.textColor = .TintD1
            checkInDateLabel.text = nil
        }
    }
    @IBOutlet weak var checkInValueLabel: UILabel! {
        didSet {
            checkInValueLabel.font = UIFont.Body()
            checkInValueLabel.textColor = .TintD1
            checkInValueLabel.text = nil
            checkInValueLabel.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.checkInTime
        }
    }
    @IBOutlet weak var checkOutDateLabel: UILabel! {
        didSet {
            checkOutDateLabel.font = UIFont.Body()
            checkOutDateLabel.textColor = .TintD1
            checkOutDateLabel.text = nil
            checkOutDateLabel.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.checkOutTimeHeader
        }
    }
    @IBOutlet weak var checkOutValueLabel: UILabel! {
        didSet {
            checkOutValueLabel.font = UIFont.Body()
            checkOutValueLabel.textColor = .TintD1
            checkOutValueLabel.text = nil
            checkOutValueLabel.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.checkOutTime
        }
    }
    @IBOutlet weak var tripImageView: UIImageView!
    @IBOutlet weak var tripTitleLabel: UILabel! {
        didSet {
            tripTitleLabel.font = UIFont.Heading4_Bold()
            tripTitleLabel.textColor = .TintD1
            tripTitleLabel.text = PILocalizedString("checkInCellLabel", comment: "Check in cell label")
        }
    }
    @IBOutlet weak var tripValueLabel: UILabel! {
        didSet {
            tripValueLabel.font = UIFont.Body()
            tripValueLabel.textColor = .TintD1
            tripValueLabel.text = PILocalizedString("checkInCellLabel", comment: "Check in cell label")
        }
    }
}
