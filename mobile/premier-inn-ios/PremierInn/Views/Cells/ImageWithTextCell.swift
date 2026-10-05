//
//  ImageWithTextCell.swift
//  PremierInn
//
//  Created by Santa Gurung on 08/07/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit

class ImageWithTextCell: UITableViewCell {
    @IBOutlet weak var containerView: UIView! {
        didSet {
            containerView.backgroundColor = .ColourLD2
        }
    }
    @IBOutlet weak var topLabel: UILabel! {
        didSet {
            topLabel.textColor = .ColourDL1
            topLabel.font = .BodySmall()
        }
    }
    @IBOutlet weak var companyNameLabel: UILabel! {
        didSet {
            companyNameLabel.textColor = .ColourDL1
            companyNameLabel.font = .Body_Semibold()
        }
    }
    @IBOutlet weak var piBusinessLogo: UIImageView! {
        didSet {
            piBusinessLogo.image = UIImage(named: "piBusinessLogoPurple")
        }
    }
}
