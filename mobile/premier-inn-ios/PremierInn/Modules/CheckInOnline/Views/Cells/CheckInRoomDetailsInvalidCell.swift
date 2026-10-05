//
//  CheckInRoomDetailsInvalidCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 12/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

class CheckInRoomDetailsInvalidCell: UITableViewCell {
    @IBOutlet weak var roomName: UILabel! {
        didSet {
            roomName.textColor = .TintD1
            roomName.font = .Heading3_Semibold()
        }
    }
    @IBOutlet weak var guestDetails: UILabel! {
        didSet {
            guestDetails.font = .Body()
        }
    }
    @IBOutlet weak var addDetailsButton: RoundedCornersButton! {
        didSet {
            addDetailsButton.titleLabel?.font = .Button1()
            addDetailsButton.setTitle(PILocalizedString("ciolAddCheckInDetailsButtonTitle"), for: .normal)
        }
    }

    var addDetailsButtonAction: (() -> Void)?

    @IBAction func addDetailsButtonDidTap(_ sender: Any) {
        addDetailsButtonAction?()
    }
}
