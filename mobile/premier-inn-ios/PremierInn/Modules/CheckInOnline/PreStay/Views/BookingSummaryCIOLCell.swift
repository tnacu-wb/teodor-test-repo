//
//  BookingSummaryCIOLCell.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 06.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class BookingSummaryCIOLCell: SimpleSeparatorsCell {
    @IBOutlet weak var hotelImageView: UIImageView! {
        didSet {
            hotelImageView.layer.cornerRadius = 8
        }
    }

    @IBOutlet weak var hotelNameLabel: UILabel! {
        didSet {
            hotelNameLabel.font = .Heading2_Bold()
            hotelNameLabel.textColor = .BaseBlack
        }
    }

    @IBOutlet weak var durationLabel: UILabel! {
        didSet {
            durationLabel.font = .Body()
            durationLabel.textColor = .ColourDL1
        }
    }

    @IBOutlet weak var summaryLabel: UILabel! {
        didSet {
            summaryLabel.font = .BodySmall_Bold()
            summaryLabel.textColor = .ColourDL1
        }
    }

    @IBOutlet weak var containerView: UIView! {
        didSet {
            containerView.layer.cornerRadius = 4
            containerView.layer.borderWidth = 1
            containerView.layer.borderColor = UIColor.TintL2.cgColor
        }
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)

        isUserInteractionEnabled = false
        selectionStyle = .none
    }

    func configure(with viewModel: BookingSummaryCIOLViewModelProtocol) {
        if let imageURL = viewModel.image {
            hotelImageView.setImage(with: imageURL)
        }
        hotelNameLabel.text = viewModel.hotelName
        durationLabel.text = viewModel.duration
        summaryLabel.text = viewModel.summary
    }
}
