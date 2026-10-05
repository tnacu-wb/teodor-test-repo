//
//  RestaurantLinksCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 23/04/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class RestaurantLinksCell: UITableViewCell {
    @IBOutlet weak var menusButton: UIButton! {
        didSet {
            menusButton.layer.cornerRadius = 4
            menusButton.layer.borderWidth = 1
            menusButton.layer.borderColor = UIColor.BasePurple.cgColor
            menusButton.titleLabel?.font = UIFont.Action1()
            menusButton.setTitle(PILocalizedString("menuLabel"), for: .normal)
            menusButton.setTitleColor(UIColor.BasePurple, for: .normal)
        }
    }

    @IBOutlet weak var allergyButton: UIButton! {
        didSet {
            allergyButton.layer.cornerRadius = 4
            allergyButton.layer.borderWidth = 1
            allergyButton.layer.borderColor = UIColor.BasePurple.cgColor
            allergyButton.titleLabel?.font = UIFont.Action1()
            allergyButton.setTitle(PILocalizedString("allergyLabel"), for: .normal)
            allergyButton.setTitleColor(UIColor.BasePurple, for: .normal)
        }
    }

    var menusAction: (() -> Void)?
    var allergyAction: (() -> Void)?

    @IBAction func viewMenusDidTap(_ sender: Any) {
        menusAction?()
    }

    @IBAction func allergyInfoDidTap(_ sender: Any) {
        allergyAction?()
    }
}
