//
//  BannerView.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 14.04.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit

class BannerView: RoundedCornersView {
    @IBOutlet weak var label: UILabel! {
        didSet {
            label.font = .Body()
            label.textColor = .BaseWhite
        }
    }
    @IBOutlet weak var image: UIImageView!

    override func setup() {
        borderColor = .Tint2
        super.setup()
        label.textColor = .ColourDL1
        backgroundColor = .Tint2.withAlphaComponent(0.2)
    }

    func configure(text: NSAttributedString) {
        label.attributedText = text
    }
}
