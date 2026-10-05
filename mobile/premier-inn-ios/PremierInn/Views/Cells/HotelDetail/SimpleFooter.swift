//
//  SimpleFooter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 13/04/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class SimpleFooter: UITableViewHeaderFooterView {
    @IBOutlet weak var lineView: UIView! {
        didSet {
            lineView.backgroundColor = UIColor.ColourLD3
        }
    }
}
