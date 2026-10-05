//
//  UpsellChosenSummaryCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 15/04/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class UpsellChosenSummaryCell: UITableViewCell {
    @IBOutlet weak var changeButton: UIButton! {
        didSet {
            changeButton.setTitle(PILocalizedString("Change", comment: ""), for: .normal)
            changeButton.titleLabel?.font = .Heading4_Semibold()
        }
    }
    @IBOutlet weak var summary: UILabel! {
        didSet {
            summary.font = .Heading4_Semibold()
        }
    }

    var buttonAction: (() -> Void)?

    @IBAction func changeButtonDidTap(_ sender: Any) {
        buttonAction?()
    }
}
