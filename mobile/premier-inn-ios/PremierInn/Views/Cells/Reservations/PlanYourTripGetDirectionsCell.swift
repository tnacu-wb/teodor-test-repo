//
//  PlanYourTripGetDirectionsCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 22/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class PlanYourTripGetDirectionsCell: UITableViewCell {
    @IBOutlet weak var directionsButton: FadeOnHighlightButton! {
        didSet {
            directionsButton.titleLabel?.font = UIFont.Button1()
            directionsButton.setTitle(PILocalizedString("planYourTripGetDirectionsButtonTitle"), for: .normal)
            directionsButton.setTitleColor(UIColor.BasePurple, for: .normal)
            directionsButton.tintColor = UIColor.BasePurple
            directionsButton.borderWidth = 1
            directionsButton.cornerRadius = 4
            directionsButton.layer.borderColor = UIColor.BasePurple.cgColor
            directionsButton.backgroundColor = UIColor.BaseWhite
        }
    }
}
