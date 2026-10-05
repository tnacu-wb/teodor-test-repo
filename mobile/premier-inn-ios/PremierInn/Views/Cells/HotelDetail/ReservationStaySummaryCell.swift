//
//  ReservationStaySummaryCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 04/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class ReservationStaySummaryCell: BorderedContentViewCell {
    override func awakeFromNib() {
        super.awakeFromNib()
        contentView.layoutMargins = UIEdgeInsets(top: 0, left: 50, bottom: 0, right: 50)
    }

    @IBOutlet weak var hotelName: UILabel! {
        didSet {
            hotelName.text = nil
            hotelName.font = .Heading1_Bold()
        }
    }
    @IBOutlet weak var stayDates: UILabel! {
        didSet {
            stayDates.text = nil
            stayDates.font = .Body_Medium()
        }
    }
    @IBOutlet weak var guestName: UILabel! {
        didSet {
            guestName.text = nil
            guestName.numberOfLines = 0
            guestName.lineBreakMode = .byWordWrapping
            guestName.font = .BodySmall_Bold()
        }
    }
    @IBOutlet weak var status: UILabel! {
        didSet {
            status.text = nil
            status.font = .SubtextSmall_Bold()
            status.layer.cornerRadius = 14
            status.clipsToBounds = true
            status.layer.borderWidth = 1.0
        }
    }

    override func setSelected(_ selected: Bool, animated: Bool) { }
}
