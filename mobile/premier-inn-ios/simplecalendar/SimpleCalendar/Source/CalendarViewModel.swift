//
//  CalendarViewModel.swift
//  Calendar
//
//  Created by Freddie Parks on 29/06/2016.
//  Copyright © 2016 Freddie Parks. All rights reserved.
//

import Foundation

struct Constants {

    struct Metrics {
        static let weekdayCellSize: CGFloat = 15
        static let monthHeaderHeight: CGFloat = 117
        static let monthFooterLastHeight: CGFloat = 111
        static let monthFooterSeparatorHeight: CGFloat = 20
        static let cellsPerRow = 7
    }
}

public struct CalendarViewModel {

    private static func cutoffDate(maxDepartureDateCount: Int) -> Date? {

        var dayComponent = DateComponents()
        // Plus one because we need to allow them to check out the day after the max arrival date
        dayComponent.day = maxDepartureDateCount

        return Calendar.current.date(byAdding: dayComponent, to: Date())
    }


    static func calendarMonthsUsing(startDate date: Date, months: Int, selectableOffset: Int?, maxDepartureDateCount: Int) -> [CalendarMonth] {

        var calendarMonths: [CalendarMonth] = []

        for i in 0..<months {
            if let monthDate = (Calendar.current as NSCalendar).date(byAdding: .month, value: i, to: date, options: NSCalendar.Options(rawValue: 0)) {
                calendarMonths.append(calendarMonthForDate(monthDate, selectableOffset: selectableOffset, maxDepartureDateCount: maxDepartureDateCount))
            }
        }

        return calendarMonths
    }

    private static func calendarMonthForDate(_ date: Date, selectableOffset: Int?, maxDepartureDateCount: Int) -> CalendarMonth {

        var dateComponents = Calendar.current.dateComponents([.month, .year, .hour], from: date)
        dateComponents.hour = 12
        dateComponents.day = 1

        guard let startOfMonth = Calendar.current.date(from: dateComponents) else { return CalendarMonth(title: "", days: []) }

        dateComponents = Calendar.current.dateComponents([.day, .weekday, .month, .year], from: startOfMonth)

        var days: [CalendarDay] = []

        let firstDayOfTheCalendar = Calendar.current.firstWeekday

        // pad start
        if let firstDayOfTheMonth = dateComponents.weekday {
            var diff = firstDayOfTheMonth - firstDayOfTheCalendar
            if diff < 0 {
                diff = Calendar.current.veryShortWeekdaySymbols.count + diff
            }

            if diff > 0 {
                for _ in 0..<diff {
                    days.append(CalendarDay(day: 0, date: nil, isSelectable: false))
                }
            }
        }

        // add actual days of month
        if let daysRange = Calendar.current.range(of: .day, in: .month, for: date) {
            for day in 1..<daysRange.upperBound {
                dateComponents.day = day

                guard let calendarDate = Calendar.current.date(from: dateComponents) else { continue }

                let isSelectable = dateIsValidBookingOption(calendarDate, cutoffOffset: selectableOffset, maxDepartureDateCount: maxDepartureDateCount)
                let calendarDay = CalendarDay(day: day, date: calendarDate, isSelectable: isSelectable)

                days.append(calendarDay)
            }
        }

        // pad end
        let remainder = days.count % 7
        if remainder > 0 {
            for _ in 1...(7 - remainder) {
                days.append(CalendarDay(day: 0, date: nil, isSelectable: false))
            }
        }

        return CalendarMonth(title: date.sectionHeaderFormattedString, days: days)
    }

    public static let weekdays: [Weekday] = {

        func index(for index: Int, plus amount: Int, in collection: [Any]) -> Int {

            guard (0..<collection.count).contains(index + amount) == false else { return index + amount }
            guard index + amount < 0 else { return 0 }

            return collection.count - 1
        }

        // weekdays shown
        var symbols = NSCalendar.current.veryShortWeekdaySymbols
        // weekdays voice over text
        var accessibilitySymbols = NSCalendar.current.weekdaySymbols
        let firstWeedayIndex = NSCalendar.current.firstWeekday - 1

        var weekendIndexes = [index(for: firstWeedayIndex, plus: -1, in: symbols)]

        guard firstWeedayIndex > 0 else {
            weekendIndexes = [index(for: NSCalendar.current.firstWeekday, plus: -1, in: symbols)]
            weekendIndexes.append(index(for: weekendIndexes.first ?? 0, plus: -1, in: symbols))

            return symbols.enumerated().map { (arg) -> Weekday in

                let (index, day) = arg
                return Weekday(text: day, accessibilityText: accessibilitySymbols[index], isWeekend: weekendIndexes.contains(symbols.firstIndex(of: day) ?? 0)) }
        }

        weekendIndexes.append(index(for: weekendIndexes.first ?? 0, plus: -1, in: symbols))

        var shuffledDays = symbols.enumerated().map { Weekday(text: $1, accessibilityText: accessibilitySymbols[$0],  isWeekend: weekendIndexes.contains($0)) }

        var sub = shuffledDays[0..<firstWeedayIndex]
        shuffledDays.removeSubrange(Range(uncheckedBounds: (0, firstWeedayIndex)))
        shuffledDays.append(contentsOf: sub)

        return shuffledDays
    }()

    // TODO: This is business logic that should be moved somewhere else
    private static func dateIsValidBookingOption(_ date: Date, cutoffOffset: Int?, maxDepartureDateCount: Int) -> Bool {
        
        guard let cutoffDate = cutoffDate(maxDepartureDateCount: maxDepartureDateCount) else { return false }

        if let cutoffOffset = cutoffOffset {

            if let cutoffOffsetDate = (Calendar.current as NSCalendar).date(byAdding: .day, value: -cutoffOffset, to: cutoffDate, options: NSCalendar.Options(rawValue: 0)) {

                return date.isInThePast == false && date.isBefore(date: cutoffOffsetDate)
            }
        }

        return (date.isInThePast == false && date.isBefore(date: cutoffDate) && date.isOnTheSameDateAs(date: cutoffDate) == false)
    }
}

public struct CalendarDay: Equatable {

    public var day: Int
    public var date: Date?
    public var isSelectable: Bool

    public var title: String? {
        guard day > 0 else { return nil }

        return String(day)
    }

    public static func == (lhs: CalendarDay, rhs: CalendarDay) -> Bool {

        return lhs.day == rhs.day && lhs.date == rhs.date
    }
}

public struct CalendarMonth: Equatable {

    public var title: String
    public var days: [CalendarDay]

    public static func == (lhs: CalendarMonth, rhs: CalendarMonth) -> Bool {

        return lhs.title == rhs.title && lhs.days == rhs.days
    }
    
}

public struct Weekday {

    public let text: String
    public let accessibilityText: String
    public let isWeekend: Bool
}
