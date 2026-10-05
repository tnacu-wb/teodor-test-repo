//
//  BookingConfirmationAccessibility.swift
//  PremierInn
//
//  Created by Nick Jones on 28/05/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

protocol BookingConfirmationAccessibilityCellCallProtocol: AnyObject {
    func didTapCallHotel()
    func didTapEmailUs()
}

class BookingConfirmationAccessibilityCell: UITableViewCell {
    weak var delegate: BookingConfirmationAccessibilityCellCallProtocol?

    @IBOutlet weak var accessibilityIcon: UIImageView! {
        didSet {
            self.accessibilityIcon.image = UIImage(named: "iconDisabled")
        }
    }
    @IBOutlet weak var accessibilityTitle: UILabel! {
        didSet {
            self.accessibilityTitle.text = PILocalizedString(
                "bookingConfirmationAccesibilityRowTitle",
                comment: "Booking confirmation accessibility row title"
            )
            self.accessibilityTitle.textColor = .TintD1
            self.accessibilityTitle.font = .Heading3_Semibold()
        }
    }
    @IBOutlet weak var accessibilityDetails: UILabel! {
        didSet {
            self.accessibilityDetails.text = PILocalizedString(
                "bookingConfirmationAccesibilityRowDetailsCustomerContactCentre",
                comment: "Booking confirmation accessibility row details"
            )
            self.accessibilityDetails.textColor = .TintD1
            self.accessibilityDetails.font = .Body()
        }
    }
    @IBOutlet weak var accessibilityButton: RoundedCornersButton! {
        didSet {
            self.accessibilityButton.setTitle(
                PILocalizedString("callHotelButtonTitle", comment: "Call hotel button title"),
                for: .normal
            )
            self.accessibilityButton.titleLabel?.textColor = .BasePurple
            self.accessibilityButton.backgroundColor = .white
            self.accessibilityButton.titleLabel?.font = .Button1()
            self.accessibilityButton.tintColor = .BasePurple
            self.accessibilityButton.layer.borderWidth = 1
            self.accessibilityButton.layer.borderColor = UIColor.BasePurple.cgColor
            self.accessibilityButton.cornerRadius = 4
        }
    }
    @IBOutlet weak var accessibilityEmailButton: RoundedCornersButton! {
        didSet {
            self.accessibilityEmailButton.setTitle(
                PILocalizedString("emailCustomerService", comment: "Email customer servuce button title"),
                for: .normal
            )
            self.accessibilityEmailButton.titleLabel?.textColor = .BasePurple
            self.accessibilityEmailButton.backgroundColor = .white
            self.accessibilityEmailButton.titleLabel?.font = .Button1()
            self.accessibilityEmailButton.tintColor = .BasePurple
            self.accessibilityEmailButton.layer.borderWidth = 1
            self.accessibilityEmailButton.layer.borderColor = UIColor.BasePurple.cgColor
            self.accessibilityEmailButton.cornerRadius = 4
        }
    }

    @IBAction func didTapCallHotel() {
        self.delegate?.didTapCallHotel()
    }

    @IBAction func didTapEmailUs() {
        self.delegate?.didTapEmailUs()
    }
}
