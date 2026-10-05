//
//  HotelMoreInfoCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 15/09/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class HotelMoreInfoCell: UITableViewCell {
    @IBOutlet weak var seeMoreInfoButton: RoundedCornersButton! {
        didSet {
            seeMoreInfoButton.titleLabel?.font = UIFont.Heading4_Semibold()
            seeMoreInfoButton.tintColor = .BasePurple
            seeMoreInfoButton.setTitle(
                PILocalizedString("hotelDetailsImportantInfo", comment: "Hotel details: important information button title"),
                for: .normal
            )
        }
    }
}
