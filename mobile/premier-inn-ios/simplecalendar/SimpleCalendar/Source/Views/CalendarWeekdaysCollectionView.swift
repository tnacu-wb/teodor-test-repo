//
//  CalendarWeekdaysCollectionView.swift
//  Calendar
//
//  Created by Freddie Parks on 29/06/2016.
//  Copyright © 2016 Freddie Parks. All rights reserved.
//

import UIKit

class CalendarWeekdaysCollectionView: UICollectionView {

    var weekdayLabelColor = UIColor.white
    var weekendLabelColor = UIColor.white
    var dayLabelFont = UIFont.boldSystemFont(ofSize: 14)

    override func awakeFromNib() {
        super.awakeFromNib()

        registerCellForNib(with: CalendarDayCell.self)

        self.delegate = self
        self.dataSource = self
        self.backgroundColor = .clear
        self.scrollsToTop = false
    }
}

extension CalendarWeekdaysCollectionView: UICollectionViewDelegate, UICollectionViewDataSource {

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {

        return CalendarViewModel.weekdays.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {

        guard let cell: CalendarDayCell = collectionView.dequeueCell(for: indexPath) else { return UICollectionViewCell() }

        let weekday = CalendarViewModel.weekdays[indexPath.row]

        cell.isSelectable = false

        cell.dayLabel.text = weekday.text
        cell.dayLabel.isAccessibilityElement = false
		cell.dayLabel.textColor = weekday.isWeekend ? weekendLabelColor : weekdayLabelColor
        cell.dayLabel.font = dayLabelFont

        return cell
    }
}

extension CalendarWeekdaysCollectionView: UICollectionViewDelegateFlowLayout {

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {

        let numberOfDays = max(CalendarViewModel.weekdays.count, 1)
        let width = (collectionView.frame.size.width) / CGFloat(numberOfDays)
        let widthConverted: CGFloat = {
            // Add the remainder to the first day of the week
            // so that we have a row with no gaps between cells
            var result = width.rounded()
            let remainder = collectionView.frame.width - result * CGFloat(Constants.Metrics.cellsPerRow)

            if indexPath.row % Constants.Metrics.cellsPerRow == 0 {
                result += remainder
            }

            return result
        }()

        return CGSize(width: widthConverted, height: Constants.Metrics.weekdayCellSize)
    }

    public func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {

        return UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
    }
}
