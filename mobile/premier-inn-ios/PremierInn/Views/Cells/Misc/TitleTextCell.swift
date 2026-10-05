//
//  TitleTextCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 04/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class TitleTextCell: UITableViewCell {
    override var textLabel: UILabel? {
        self.title
    }

    @IBOutlet weak var title: UILabel! {
        didSet {
            title.font = .Heading3_Semibold()
        }
    }
}
