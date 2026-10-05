//
//  MultiLineSubtitleCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 27/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

class MultiLineSubtitleCell: UITableViewCell {
    @IBOutlet var title: UILabel!
    @IBOutlet var subtitle: UILabel!
    @IBOutlet weak var bottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var topConstraint: NSLayoutConstraint!
    @IBOutlet weak var titleSubtitleGapConstraint: NSLayoutConstraint!
}
