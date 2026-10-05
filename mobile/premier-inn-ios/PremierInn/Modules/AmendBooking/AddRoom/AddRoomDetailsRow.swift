//
//  AddRoomDetailsRow.swift
//  PremierInn
//
//  Created by Nick Jones on 23/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import UIKit

class AddRoomDetailsRow: UITableViewCell {
    @IBOutlet weak var roomNumber: UILabel! {
        didSet {
            roomNumber.accessibilityIdentifier = "roomNumberLabel"
            roomNumber.font = .Heading1_Semibold()
        }
    }
    @IBOutlet weak var changeLabel: UILabel! {
        didSet {
            changeLabel.accessibilityIdentifier = "changeLabel"
            changeLabel.font = .Action1()
        }
    }
    @IBOutlet weak var adultsChildrenAndRoomDescription: UILabel! {
        didSet {
            adultsChildrenAndRoomDescription.accessibilityIdentifier = "descriptionLabel"
            adultsChildrenAndRoomDescription.font = .Body()
        }
    }
    @IBOutlet weak var price: UILabel! {
        didSet {
            price.accessibilityIdentifier = "priceLabel"
            price.font = .Heading2_Semibold()
        }
    }
    @IBOutlet weak var wholeCellButton: UIButton!
}
