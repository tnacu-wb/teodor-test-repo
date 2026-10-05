//
//  HotelDetailsCriteriaSummaryCell.swift
//  PremierInn
//
//  Created by Nick Jones on 11/07/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

protocol CriteriaSummaryCellDelegate: AnyObject {
    func datesButtonTapped()
    func guestsAndRoomsButtonTapped()
}

class HotelDetailsCriteriaSummaryCell: UITableViewCell {
    @IBOutlet weak var stackView: UIStackView!
}
