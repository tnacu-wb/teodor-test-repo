//
//  DropShadowContainer.swift
//  PremierInn
//
//  Created by Freddie Parks on 15/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class DropShadowContainer: RoundedCornersView {
    override func awakeFromNib() {
        super.awakeFromNib()

        layer.masksToBounds = false
        layer.shadowOffset = CGSize(width: 2.0, height: 2.5)
        layer.shadowColor = UIColor.black.cgColor
        layer.shadowOpacity = 0.7
		layer.shouldRasterize = true
		layer.rasterizationScale = UIScreen.main.scale
    }
}
