//
//  HotelAccessibilityInfoCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 30/05/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

protocol HotelAccessibilityInfoCellDelegate: AnyObject {
    func callButtonDidTap()
    func emailButtonDidTap()
    func accessibleRoomsButtonDidTap()
    func disabledAccessButtonDidTap()
}

class HotelAccessibilityInfoCell: UITableViewCell {
    @IBOutlet weak var hotelAccessibilityTitle: UILabel! {
        didSet {
            hotelAccessibilityTitle.font = .Heading2_Semibold()
            hotelAccessibilityTitle.text = PILocalizedString("bookingConfirmationAccesibilityRowTitle")
        }
    }
    @IBOutlet weak var hotelInfo: UILabel! {
        didSet {
            hotelInfo.font = .BodySmall()
        }
    }
    @IBOutlet weak var contactInfo: UILabel! {
        didSet {
            contactInfo.font = .BodySmall()
        }
    }
    @IBOutlet weak var callButton: RoundedCornersButton! {
        didSet {
            callButton.titleLabel?.font = .Button2()
        }
    }
    @IBOutlet weak var emailButton: RoundedCornersButton! {
        didSet {
            emailButton.titleLabel?.font = .Button2()
        }
    }
    @IBOutlet weak var ourAccessibleRoomsButton: UIButton! {
        didSet {
            ourAccessibleRoomsButton.titleLabel?.font = .Action1()
            ourAccessibleRoomsButton.setTitle(PILocalizedString("Our accessible rooms"), for: .normal)
        }
    }
    @IBOutlet weak var disabledAccessInfoButton: UIButton! {
        didSet {
            disabledAccessInfoButton.titleLabel?.font = .Action1()
            disabledAccessInfoButton.setTitle(PILocalizedString("Disabled access information"), for: .normal)
        }
    }

    weak var delegate: HotelAccessibilityInfoCellDelegate?

    @IBAction func callButtonDidTap(_ sender: Any) {
        delegate?.callButtonDidTap()
    }

    @IBAction func accessibleRoomsButtonDidTap(_ sender: Any) {
        delegate?.accessibleRoomsButtonDidTap()
    }

    @IBAction func disabledAccessButtonDidTap(_ sender: Any) {
        delegate?.disabledAccessButtonDidTap()
    }

    @IBAction func emailButtonDidTap() {
        delegate?.emailButtonDidTap()
    }
}
