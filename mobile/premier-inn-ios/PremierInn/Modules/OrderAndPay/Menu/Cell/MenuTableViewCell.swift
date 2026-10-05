//
//  MenuTableViewCell.swift
//  PremierInn
//
//  Created by Simon Antoine on 25/08/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

class MenuTableViewCell: UITableViewCell {
    @IBOutlet weak var containerView: UIView!
    @IBOutlet weak var addButton: UIButton!
    @IBOutlet weak var price: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    var count = 0

    @IBAction func buttonPressed(_ sender: Any) {
    }
}
