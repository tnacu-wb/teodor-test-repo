//
//  HotelCheckAvailabilityCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 04/04/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class HotelCheckAvailabilityCell: UITableViewCell {
    @IBOutlet weak var title: UILabel! {
        didSet {
            title.font = UIFont.Heading3_Semibold()
        }
    }
    @IBOutlet weak var checkAvailability: RoundedCornersButton! {
        didSet {
            checkAvailability.titleLabel?.font = UIFont.Button1()
        }
    }
    @IBOutlet weak var viewBox: UIView!

    override func awakeFromNib() {
        super.awakeFromNib()

        title.text = PILocalizedString("checkAvailabilityDescription", comment: "Check availability description")
        title.textColor = .TintD1
        checkAvailability.setTitle(PILocalizedString("selectDatesButtonTitle"), for: .normal)
        checkAvailability.setTitleColor(.BaseWhite, for: .normal)
        checkAvailability.backgroundColor = .Tint1

        viewBox.layer.borderColor = UIColor.TintL2.cgColor
        viewBox.layer.borderWidth = 1.0
    }
}
