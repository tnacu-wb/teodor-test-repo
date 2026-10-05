//
//  SimpleSubtitleCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 24/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class SimpleSubtitleCell: SimpleSeparatorsCell {
    // MARK: - Views

    @IBOutlet weak var titleLeadingConstraint: NSLayoutConstraint!
    @IBOutlet weak var subtitleLeadingConstraint: NSLayoutConstraint!
    @IBOutlet var title: UILabel! {
        didSet {
            title.font = .Body()
        }
    }
    @IBOutlet var subtitle: UILabel! {
        didSet {
            subtitle.font = .Body()
        }
    }
    @IBOutlet var solidSeparator: UIView!

    // Constraints

    @IBOutlet weak var bottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var topConstraint: NSLayoutConstraint!
    @IBOutlet weak var titleSubtitleGapConstraint: NSLayoutConstraint!
}
