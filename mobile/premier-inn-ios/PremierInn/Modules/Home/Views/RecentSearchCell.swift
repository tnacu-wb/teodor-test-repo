//
//  RecentSearchCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 24/09/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class RecentSearchCell: BorderedContentViewCell {
    override var imageView: UIImageView? {
        icon
    }

    override var textLabel: UILabel? {
        suggestionTitle
    }

    override var detailTextLabel: UILabel? {
        criteriaSummary
    }

    @IBOutlet weak var icon: UIImageView!
    @IBOutlet weak var suggestionTitle: UILabel!
    @IBOutlet weak var criteriaSummary: UILabel!
}
