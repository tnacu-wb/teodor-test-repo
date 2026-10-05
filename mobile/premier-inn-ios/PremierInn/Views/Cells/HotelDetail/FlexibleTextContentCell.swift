//
//  FlexibleTextContentCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 20/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class FlexibleTextContentCell: SimpleSeparatorsCell {
    @IBOutlet weak var contentContainer: UIView!
    @IBOutlet weak var contentContainerBottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var contentContainerTopConstraint: NSLayoutConstraint!
    @IBOutlet weak var contentContainerTrailingConstraint: NSLayoutConstraint!
    @IBOutlet weak var contentContainerLeadingConstraint: NSLayoutConstraint!

    @IBOutlet var content: UILabel! {
        didSet {
            content.font = UIFont.Body()
            content.textColor = .TintD1
        }
    }
    @IBOutlet var messageTopConstraint: NSLayoutConstraint!
    @IBOutlet var messageBottomConstraint: NSLayoutConstraint!
    @IBOutlet var messageLeadingConstraint: NSLayoutConstraint!
    @IBOutlet var messageTrailingConstraint: NSLayoutConstraint!

    var padding: UIEdgeInsets {
        get {
            UIEdgeInsets(
                top: messageTopConstraint.constant,
                left: messageLeadingConstraint.constant,
                bottom: messageBottomConstraint.constant,
                right: messageTrailingConstraint.constant
            )
        }
        set {
            messageTopConstraint.constant = newValue.top
            messageTrailingConstraint.constant = newValue.right
            messageBottomConstraint.constant = newValue.bottom
            messageLeadingConstraint.constant = newValue.left
        }
    }
}
