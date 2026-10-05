//
//  HotelNotesCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 19/09/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class HotelNotesCell: SimpleSeparatorsCell {
    @IBOutlet weak var noteLabel: UILabel! {
        didSet {
            noteLabel.font = UIFont.Body()
        }
    }
    @IBOutlet var icon: UIImageView!
    @IBOutlet var iconWidthConstraint: NSLayoutConstraint!
    @IBOutlet var iconHeightConstraint: NSLayoutConstraint!
}
