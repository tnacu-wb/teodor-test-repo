//
//  SubtitleDisclosureCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 22/12/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class SubtitleDisclosureCell: DisclosureCell {
    override func awakeFromNib() {
        super.awakeFromNib()

        textLabel?.font = .Action1()
        detailTextLabel?.font = .BodySmall()
        backgroundColor = .white
        layoutMargins = UIEdgeInsets(top: 0, left: 8, bottom: 0, right: 8)
    }
}
