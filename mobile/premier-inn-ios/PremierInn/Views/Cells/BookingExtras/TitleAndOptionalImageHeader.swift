//
//  TitleAndOptionalImageHeader.swift
//  PremierInn
//
//  Created by Freddie Parks on 28/09/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class TitleAndOptionalImageHeader: UITableViewHeaderFooterView {
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = UIFont.Heading1_Semibold()
            titleLabel.textColor = .TintD1

            titleLabel.superview?.backgroundColor = backgroundColor ?? contentView.backgroundColor
        }
    }
    @IBOutlet weak var imageView: UIImageView!
}
