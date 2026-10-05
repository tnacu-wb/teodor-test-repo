//
//  DropdownActivityHeader.swift
//  PremierInn
//
//  Created by Freddie Parks on 08/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class DropdownActivityHeader: UITableViewHeaderFooterView {
    @IBOutlet weak var title: UILabel! {
        didSet {
            title.font = .Body_Semibold()
        }
    }
}
