//
//  TripAdvisorHeader.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 29/11/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class TripAdvisorHeader: UITableViewHeaderFooterView {
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = UIFont.Heading4_Semibold()
            titleLabel.textColor = UIColor.TintD2
        }
    }
    @IBOutlet weak var imageView: UIImageView! {
        didSet {
            imageView.image = UIImage.init(imageLiteralResourceName: "tripAdvisorLogo")
        }
    }
}
