//
//  AddRoomCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 28/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

protocol AddRoomCellDelegate: AnyObject {
    func addRoomCellDidTap(cell: AddRoomCell)
}

class AddRoomCell: TableCellWithError {
    weak var delegate: AddRoomCellDelegate?

    @IBOutlet weak var buttonLeadingConstraint: NSLayoutConstraint!
    @IBOutlet weak var buttonTrailingConstraint: NSLayoutConstraint!
    @IBOutlet weak var buttonHeightConstraint: NSLayoutConstraint!
    @IBOutlet weak var addButton: AddRoomButton!

    @IBAction func addRoomButtonDidTap(_ sender: UIButton) {
        delegate?.addRoomCellDidTap(cell: self)
    }
}
