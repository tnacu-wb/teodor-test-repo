//
//  HDPInfoCellTableViewCell.swift
//  PremierInn
//
//  Created by andrei.cojocaru on 8/12/24.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class HDPInfoCellTableViewCell: UITableViewCell {
    private var heightConstraint: NSLayoutConstraint?

    @IBOutlet weak var iconImageView: UIImageView!

    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.setupLabel(
                font: .Body(),
                lineHeightMultiple: 1.23,
                kern: 0,
                textColor: .TintD1,
                numberOfLines: 1,
                accessibilityIdentifier: "infoCellTitleLabel"
            )
        }
    }

    func setHeight(_ height: CGFloat?) {
        heightConstraint?.isActive = false

        if let height = height {
            heightConstraint = contentView.heightAnchor.constraint(equalToConstant: height)
            heightConstraint?.priority = .init(999)
            heightConstraint?.isActive = true
        }
    }

    override func prepareForReuse() {
        super.prepareForReuse()
        heightConstraint?.isActive = false
        heightConstraint = nil
    }
}
