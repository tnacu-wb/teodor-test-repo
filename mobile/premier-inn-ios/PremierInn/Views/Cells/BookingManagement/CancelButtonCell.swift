//
//  CancelButtonCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 24/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

protocol CancelButtonCellDelegate: AnyObject {
    func cancelButtonDidTap(cell: CancelButtonCell) throws
}

class CancelButtonCell: UITableViewCell {
    weak var delegate: CancelButtonCellDelegate?

    @IBOutlet weak var buttonLeadingConstraint: NSLayoutConstraint!
    @IBOutlet weak var buttonHeightConstraint: NSLayoutConstraint!
    @IBOutlet weak var cancelButton: RoundedCornersButton! {
        didSet {
            cancelButton.accessibilityIdentifier = "cancelButtonAcc"
            cancelButton.setTitle(
                PILocalizedString("cancelBookingButtonTitle", comment: "Cancel booking button title"),
                for: .normal
            )
            cancelButton.titleLabel?.font = .Button1()
            cancelButton.setTitleColor(.white, for: .normal)
            cancelButton.backgroundColor = UIColor.paleRed
        }
    }
    @IBOutlet weak var separatorLine: UIView! {
        didSet {
            separatorLine.backgroundColor = UIColor.TintL2
        }
    }
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!

    @IBAction func cancelButtonDidTap(_ sender: UIButton) {
        do {
            try delegate?.cancelButtonDidTap(cell: self)
        } catch {
            print(error)
        }
    }
}
