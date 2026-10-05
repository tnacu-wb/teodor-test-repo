//
//  ConfirmCheckInCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 13/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

protocol CheckInConfirmationCellDelegate: class {
    func confirmCheckInDidTap()
    func showTermsDidTap()
}

class CheckInConfirmationCell: UITableViewCell {
    @IBOutlet weak var confirmCheckInButton: RoundedCornersButton!
    @IBOutlet weak var details: UILabel!
    @IBOutlet weak var terms: UILabel!

    weak var delegate: CheckInConfirmationCellDelegate?

    @IBAction func confirmCheckInButtonDidTap(_ sender: Any) {
        delegate?.confirmCheckInDidTap()
    }

    @IBAction func termsLabelDidTap(_ sender: Any) {
        delegate?.showTermsDidTap()
    }
}
