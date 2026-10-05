//
//  FormekaTableViewCell.swift
//  Formeka
//
//  Created by Freddie Parks on 17/06/2019.
//  Copyright © 2019 Marcello Mascia. All rights reserved.
//

import UIKit

open class FormekaTableViewCell: UITableViewCell {

    open var shouldIgnoreMargins: Bool = false

    open override func layoutSubviews() {
        super.layoutSubviews()

        guard !shouldIgnoreMargins else { return }

        if let cellMargins = (self as? FormekaCellMargins)?.margins {

            contentView.frame = contentView.frame.inset(by: cellMargins)
            separatorInset = UIEdgeInsets(top: 0, left: cellMargins.left, bottom: cellMargins.bottom, right: cellMargins.right)
        }
    }
}
