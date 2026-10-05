//
//  MyBookingsErrorsView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 11/08/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

protocol MyBookingsErrorsViewCellDelegate: AnyObject {
    func actionButtonDidTap(cell: MyBookingsErrorsViewCell)
    func findBookingDidTap(cell: MyBookingsErrorsViewCell)
}

class MyBookingsErrorsViewCell: UITableViewCell {
    weak var delegate: MyBookingsErrorsViewCellDelegate?

    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .Heading1_ExtraBold()
            titleLabel.textColor = UIColor.BasePurple
            titleLabel.accessibilityTraits.insert(.header)
        }
    }
    @IBOutlet weak var subtitleLabel: UILabel! {
        didSet {
            subtitleLabel.font = .Body_Medium()
            subtitleLabel.textColor = UIColor.TintD1
        }
    }
    @IBOutlet weak var actionButton: FadeOnHighlightButton! {
        didSet {
            actionButton.titleLabel?.font = .Button1()
            actionButton.setTitleColor(.BaseWhite, for: .normal)
            actionButton.backgroundColor = .BasePurple
        }
    }
    @IBOutlet weak var findBookingButton: RoundedCornersButton! {
        didSet {
            findBookingButton.titleLabel?.font = .Button1()
            findBookingButton.setTitleColor(.BasePurple, for: .normal)
            findBookingButton.backgroundColor = .BaseWhite
        }
    }
    @IBOutlet weak var orLabel: UILabel! {
        didSet {
            orLabel.font = .Body_Medium()
            orLabel.textColor = .TintD2
        }
    }

    @IBAction func actionButtonDidTap(_ sender: UIButton) {
        delegate?.actionButtonDidTap(cell: self)
    }

    @IBAction func findBookingButtonDidTap(_ sender: UIButton) {
        delegate?.findBookingDidTap(cell: self)
    }
}
