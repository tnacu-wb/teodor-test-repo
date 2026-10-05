//
//  GuestsSummaryHeader.swift
//  PremierInn
//
//  Created by Freddie Parks on 24/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class ActionableHeader: UITableViewHeaderFooterView {
    @IBOutlet var heading: UILabel! {
        didSet {
            heading.font = .Heading3_Semibold()
        }
    }
    @IBOutlet var actionButton: SimpleTextButton! {
        didSet {
            actionButton.titleLabel?.font = .Action1()
        }
    }
    @IBOutlet weak var leadingConstraint: NSLayoutConstraint!
}
