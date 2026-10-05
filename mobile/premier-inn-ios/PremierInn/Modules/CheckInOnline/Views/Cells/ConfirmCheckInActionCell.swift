//
//  ConfirmCheckInActionCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 14/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

protocol ConfirmCheckInActionCellDelegate: AnyObject {
    func confirmDidTap()
    func termsDidTap()
}

class ConfirmCheckInActionCell: UITableViewCell {
    override func awakeFromNib() {
        super.awakeFromNib()

        let guestureRecogniser = UITapGestureRecognizer(target: self, action: #selector(termsDidTap))
        terms.isUserInteractionEnabled = true
        terms.addGestureRecognizer(guestureRecogniser)
    }

    weak var delegate: ConfirmCheckInActionCellDelegate?

    @IBOutlet weak var confirmButton: RoundedCornersButton! {
        didSet {
            confirmButton.titleLabel?.font = .Button1()
            confirmButton.setTitle(PILocalizedString("ciolConfirmCheckInButtonTitle"), for: .normal)
        }
    }
    @IBOutlet weak var details: UILabel!
    @IBOutlet weak var terms: UILabel!

    @IBAction func confirmButtonDidTap(_ sender: Any) {
        delegate?.confirmDidTap()
    }

    @objc private func termsDidTap() {
        delegate?.termsDidTap()
    }
}
