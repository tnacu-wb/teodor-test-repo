//
//  Extensions.swift
//  SimpleCalendar
//
//  Created by Marcello Mascia on 26/07/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

extension UICollectionView {

    func registerCellForNib(with type: UICollectionViewCell.Type) {

        let identifier = String(describing: type)

        register(UINib(nibName: identifier, bundle: .module), forCellWithReuseIdentifier: identifier)
    }

    func registerSupplementaryViewNib(with type: UICollectionReusableView.Type, kind: String) {

        let identifier = String(describing: type)

        register(UINib(nibName: identifier, bundle: .module), forSupplementaryViewOfKind: kind, withReuseIdentifier: identifier)
    }

    func dequeueCell<T: UICollectionViewCell>(for indexPath: IndexPath) -> T? {

        return dequeueReusableCell(withReuseIdentifier: String(describing: T.self), for: indexPath) as? T
    }
}

public extension Date {

    var isInThePast: Bool {
        let currentCalendar = Calendar.current
        let dateComponents = currentCalendar.dateComponents([.day, .month, .year], from: Date())

        guard let today = currentCalendar.date(from: dateComponents) else { return false }

        return isBefore(date: today)
    }

    var isWeekend: Bool {
        return Calendar.current.isDateInWeekend(self)
    }

    func isOnTheSameDateAs(date: Date) -> Bool {

        return Calendar.current.isDate(self, inSameDayAs: date)
    }

    func isBefore(date: Date) -> Bool {

        return self.compare(date) == ComparisonResult.orderedAscending
    }

    var sectionHeaderFormattedString: String {
        return DateFormatter.sectionHeaderDateFormatter.string(from: self)
    }

    static func englishLocaleDateString(date: Date) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.locale = Locale(identifier: "en_GB")
        dateFormatter.dateStyle = .full
        return dateFormatter.string(from: date)
    }
}

extension DateFormatter {

    static let sectionHeaderDateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMMM YYYY"

        return formatter
    }()
}
