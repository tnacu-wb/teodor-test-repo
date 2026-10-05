//
//  CheckInOuHDPtCell.swift
//  PremierInn
//
//  Created by andrei.cojocaru on 8/1/24.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class CheckInOutHDPCell: UITableViewCell {
    @IBOutlet weak var checkInLabel: UILabel! {
        didSet {
            checkInLabel.font = .Body_Bold()
            checkInLabel.textColor = .BasePurple
            checkInLabel.text = PILocalizedString("checkInCellLabel", comment: "Check in cell label")
            checkInLabel.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.checkInTimeHeader
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
    @IBOutlet weak var checkInImageView: UIImageView! {
        didSet {
            checkInImageView.image = .checkInImage
        }
    }
    @IBOutlet weak var checkOutLabel: UILabel! {
        didSet {
            checkOutLabel.font = .Body_Bold()
            checkOutLabel.textColor = .BasePurple
            checkOutLabel.text = PILocalizedString("checkOutCellLabel", comment: "Check out cell label")
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
    @IBOutlet weak var checkOutImageView: UIImageView! {
        didSet {
            checkOutImageView.image = .checkOutImage
        }
    }
}
