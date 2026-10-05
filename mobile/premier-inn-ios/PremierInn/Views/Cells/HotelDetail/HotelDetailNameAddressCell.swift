//
//  HotelDetailNameAddressCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 12/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class HotelDetailNameAddressCell: UITableViewCell {
    @IBOutlet weak var nameLabel: UILabel! {
        didSet {
            nameLabel.text = nil
            nameLabel.font = UIFont.Heading1_Semibold()
            nameLabel.textColor = UIColor.TintD1
        }
    }
    @IBOutlet weak var addressLabel: UILabel! {
        didSet {
            addressLabel.text = nil
            addressLabel.font = UIFont.Body()
            addressLabel.textColor = UIColor.TintD1
        }
    }

    func addressTextView(withText text: String?) {
        guard let text = text else { return }
        addressLabel.text = text
    }

    func nameLabelWithText(_ text: String) {
        let paragraphStyleWithSpacing = NSMutableParagraphStyle()
        paragraphStyleWithSpacing.lineSpacing = 5

        let attributes = [
            NSAttributedString.Key.font: UIFont.Heading1_Semibold(),
            NSAttributedString.Key.paragraphStyle: paragraphStyleWithSpacing,
            NSAttributedString.Key.foregroundColor: UIColor.TintD1
        ]

        nameLabel.attributedText = NSMutableAttributedString(string: text, attributes: attributes)
    }
}
