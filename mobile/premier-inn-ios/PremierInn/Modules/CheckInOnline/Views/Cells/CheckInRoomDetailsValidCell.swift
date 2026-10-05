//
//  CheckInRoomDetailsValidCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 13/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

class CheckInRoomDetailsValidCell: UITableViewCell {
    @IBOutlet weak var roomName: UILabel! {
        didSet {
            roomName.font = .Heading3_Semibold()
            roomName.textColor = .TintD1
        }
    }
    @IBOutlet weak var guestDetails: UILabel! {
        didSet {
            guestDetails.font = .Body()
        }
    }
    @IBOutlet weak var addDetailsButton: UIButton! {
        didSet {
            addDetailsButton.titleLabel?.font = .Action1()
            addDetailsButton.setTitle(PILocalizedString("Change"), for: .normal)
        }
    }

    var addDetailsButtonAction: (() -> Void)?

    @IBAction func addDetailsButtonDidTap(_ sender: Any) {
        addDetailsButtonAction?()
    }
}
