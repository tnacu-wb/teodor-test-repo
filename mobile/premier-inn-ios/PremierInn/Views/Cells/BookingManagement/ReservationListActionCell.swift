//
//  ActionIconCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 04/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class ReservationListActionCell: BorderedContentViewCell {
    // MARK: - Views

    override var textLabel: UILabel? {
        self.title
    }

    @IBOutlet weak var title: UILabel! {
        didSet {
            title.text = nil
            title.font = .Body()
            title.textColor = .ColourDL5
        }
    }
    @IBOutlet weak var icon: UIImageView! {
        didSet {
            icon.tintColor = .ColourDL5
        }
    }
    @IBOutlet weak var indicator: UIImageView! {
        didSet {
            indicator.image = UIImage(named: "discloseIndicator")?.imageWithColor(.ColourDL7)
        }
    }

    // Constraints

    @IBOutlet weak var iconWidthConstraint: NSLayoutConstraint!
    @IBOutlet weak var topBorderView: UIView!
    @IBOutlet weak var bottomBorderView: UIView!
}
