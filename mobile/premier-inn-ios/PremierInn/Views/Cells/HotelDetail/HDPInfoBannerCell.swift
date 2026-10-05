//
//  HDPInfoBannerCell.swift
//  PremierInn
//
//  Created by andrei.cojocaru on 8/12/24.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class HDPInfoBannerCell: UITableViewCell {
    @IBOutlet weak var containerView: UIView! {
        didSet {
            containerView.layer.borderColor = UIColor.Tint2.cgColor
            containerView.layer.borderWidth = 1
            containerView.layer.cornerRadius = 3
        }
    }

    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.setupLabel(
                font: .BodySmall(),
                textColor: .TintD1,
                accessibilityIdentifier: "infoBannerTitleLabel"
            )
        }
    }
}
