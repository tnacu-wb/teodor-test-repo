//
//  CalendarHeader.swift
//  Calendar
//
//  Created by Freddie Parks on 29/06/2016.
//  Copyright © 2016 Freddie Parks. All rights reserved.
//

import UIKit

class CalendarHeader: UICollectionReusableView {

    @IBOutlet weak var monthLabel: UILabel!
    @IBOutlet weak var collectionView: CalendarWeekdaysCollectionView! {
        didSet {
            collectionView.registerCellForNib(with: CalendarDayCell.self)
        }
    }
    @IBOutlet weak var separatorLine: UIView!

    func setCustomColours(weekday: UIColor, weekend: UIColor, month: UIColor, separator: UIColor) {

        collectionView.weekdayLabelColor = weekday
        collectionView.weekendLabelColor = weekend
        monthLabel.textColor = month
        separatorLine.backgroundColor = separator
    }

    func setCustomFonts(day: UIFont, month: UIFont) {

        collectionView.dayLabelFont = day
        monthLabel.font = month
    }
}
