//
//  CriteriaSummaryView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 16/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class CriteriaSummaryView: UIView {
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.text = nil
            titleLabel.font = UIFont.BodySmall_Semibold()
            titleLabel.textColor = .white
            titleLabel.backgroundColor = .clear
        }
    }
    @IBOutlet weak var subtitleLabel: UILabel! {
        didSet {
            subtitleLabel.text = nil
            subtitleLabel.font = UIFont.SubtextSmall()
            subtitleLabel.textColor = .white
            subtitleLabel.backgroundColor = .clear
        }
    }
}
