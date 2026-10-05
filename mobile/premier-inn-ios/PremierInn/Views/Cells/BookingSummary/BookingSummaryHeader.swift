//
//  BookingSummaryHeader.swift
//  PremierInn
//
//  Created by Marcello Mascia on 24/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class BookingSummaryHeader: UITableViewHeaderFooterView {
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.textColor = UIColor.TintD1
            titleLabel.font = .Heading2_Semibold()
        }
    }
}
