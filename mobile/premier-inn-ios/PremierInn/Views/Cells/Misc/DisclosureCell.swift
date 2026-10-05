//
//  DisclosureCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 22/12/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class DisclosureCell: SimpleSeparatorsCell {
    func setAccessoryIcon(icon: UIImage? = UIImage(named: "discloseIndicator")) {
        accessoryView = UIImageView(image: icon?.imageWithColor(.ColourDL7))
        accessoryView?.frame = CGRect(x: 0, y: 0, width: 16, height: 16)
        accessoryView?.contentMode = .scaleAspectFit
    }

    override func awakeFromNib() {
        super.awakeFromNib()

        accessoryView = UIImageView(image: UIImage(named: "discloseIndicator")?.imageWithColor(.ColourDL7))
    }
}
