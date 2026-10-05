//
//  FormekaErrorBannerCell.swift
//  Formeka
//
//  Created by Marcello Mascia on 22/07/2017.
//  Copyright © 2017 Marcello Mascia. All rights reserved.
//

import UIKit

public class FormekaErrorBannerCell: FormekaTableViewCell {

    @IBOutlet public weak var icon: UIImageView! {
        didSet {
            icon.image = UIImage(named: "errorIconRed", in: .module, with: nil)
        }
    }
    @IBOutlet public weak var message: UILabel!
}
