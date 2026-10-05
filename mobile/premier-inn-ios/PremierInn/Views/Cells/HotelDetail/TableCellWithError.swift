//
//  TableCellWithError.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 28/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class TableCellWithError: UITableViewCell {
    @IBOutlet weak var errorView: ErrorViewInCell! {
        didSet {
            errorView.accessibilityIdentifier = "errorCellAcc"
        }
    }
    @IBOutlet weak var errorViewHeightConstraint: NSLayoutConstraint!

    override func prepareForReuse() {
        super.prepareForReuse()

        // Resetting the value to zero since these cells are reusable
        errorViewHeightConstraint.constant = 0
    }
}
