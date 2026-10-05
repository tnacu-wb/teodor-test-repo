//
//  FoodContentCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 07/06/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

class FoodContentCell: ContentCell {
    @IBOutlet weak var restaurantImage: UIImageView!
    @IBOutlet weak var imageViewNormalHeight: NSLayoutConstraint!
    @IBOutlet weak var imageViewZeroHeight: NSLayoutConstraint!
    @IBOutlet weak var foodContentStackView: UIStackView!
}
