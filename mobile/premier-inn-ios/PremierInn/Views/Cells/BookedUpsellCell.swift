//
//  BookedUpsellCell.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 01.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class BookedUpsellCell: SimpleSeparatorsCell {
    @IBOutlet weak var upsellImageView: UIImageView! {
        didSet {
            upsellImageView.layer.cornerRadius = 8
        }
    }

    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .Body_Semibold()
        }
    }

    @IBOutlet weak var descriptionLabel: UILabel! {
        didSet {
            descriptionLabel.font = .BodySmall()
        }
    }

    @IBOutlet weak var unavailableLabel: UILabel!

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)

        isUserInteractionEnabled = false
        selectionStyle = .none
    }

    func configure(with viewModel: BookedUpsellViewModel) {
        titleLabel.text = viewModel.title
        titleLabel.isEnabled = viewModel.isAvailable
        descriptionLabel.text = viewModel.summary
        descriptionLabel.isEnabled = viewModel.isAvailable
        descriptionLabel.isHidden = !viewModel.isAvailable
        unavailableLabel.attributedText = viewModel.unavailableSummary
        unavailableLabel.isHidden = viewModel.isAvailable
        upsellImageView.image = nil
        upsellImageView.layer.opacity = viewModel.isAvailable ? 1 : 0.7
        if let imageURL = viewModel.image {
            upsellImageView.setImage(with: imageURL)
        }
    }
}
