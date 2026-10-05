//
//  SelectRateHeader.swift
//  PremierInn
//
//  Created by Freddie Parks on 08/10/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class RateSelectHeader: UITableViewHeaderFooterView {
    @IBOutlet weak var dates: UILabel! {
        didSet {
            dates.textColor = .TintD1
            dates.font = .Heading3_Semibold()
        }
    }
    @IBOutlet weak var guests: UILabel! {
        didSet {
            guests.textColor = .TintD1
            guests.font = .Body()
        }
    }
    @IBOutlet weak var closeButton: RoundedCornersButtonBiggerTapArea! {
        didSet {
            closeButton.minimumHitArea = CGSize(width: 44, height: 44)
            closeButton.setTitleColor(.BasePurple, for: .normal)
            closeButton.titleLabel?.font = UIFont.Body()
        }
    }
}
