//
//  NSDate+Extensions.swift
//  PremierInn
//
//  Created by Freddie Parks on 03/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation

let secondsInDay: TimeInterval = 86400
let secondsInHour: TimeInterval = 3600
let secondsInMinute: TimeInterval = 60

extension Date {
    // MARK: - Computed Properties

    var isToday: Bool {
        Calendar.current.isDateInToday(self)
    }

    var isLessThan48HoursFromNow: Bool {
        let now = Date()
        let difference = self.timeIntervalSince(now)

        let hoursDifference = difference / 3600
        return abs(hoursDifference) < 48
    }

    var ignoringTime: Date {
        let dateComponents = Calendar.current.dateComponents([.year, .month, .day], from: self)
        return Calendar.current.date(from: dateComponents) ?? self
    }

    var localizedShortDayMonthStringFormat: String {
        DateFormatter.shortDayMonthStringFormatter.string(from: self)
    }

    var localizedShortDayMonthYearStringFormat: String {
        DateFormatter.shortDayMonthYearStringFormatter.string(from: self)
    }

    var localizedMediumDayMonthStringFormat: String {
        DateFormatter.mediumDayMonthStringFormatter.string(from: self)
    }

    var localizedSlashDayMonthYearStringFormat: String {
        DateFormatter.slashDayMonthYearStringFormatter.string(from: self)
    }

    var analyticsDayFormat: String {
        DateFormatter.analyticsDayFormatter.string(from: self)
    }

    var fullDateFormat: String {
        DateFormatter.fullDateFormatter.string(from: self)
    }

    var analyticsTimeFormat: String {
        DateFormatter.analyticsTimeFormatter.string(from: self)
    }

    var year: Int? {
        let calendar = Calendar.current
        let components = calendar.dateComponents([.year], from: self)

        return components.year
    }

    // MARK: - Methods

    func secondsSinceMidnight(timeZone: TimeZone?) -> TimeInterval {
        var calendar = Calendar(identifier: Calendar.Identifier.gregorian)

        if let timeZone = timeZone {
            calendar.timeZone = timeZone
        }

        let startOfTheDay = calendar.startOfDay(for: self)
        let result = self.timeIntervalSince(startOfTheDay)

        return result
    }

    func dateUsing(existingDate date: Date, secondsSinceMidnight seconds: TimeInterval) -> Date? {
        var dateComponents = Calendar.current.dateComponents([.day, .month, .year, .hour, .minute, .second], from: date)

        dateComponents.hour = Int((seconds - (seconds.truncatingRemainder(dividingBy: secondsInHour))) / secondsInHour)
        dateComponents.minute = Int((seconds - (seconds.truncatingRemainder(dividingBy: secondsInMinute))) / secondsInMinute)
        dateComponents.second = Int(seconds.truncatingRemainder(dividingBy: secondsInHour))

        return Calendar.current.date(from: dateComponents)
    }

    func isLessThanTwentyFourHoursUntilCheckin(on date: Date) -> Bool {
        guard self.dateByAddingUnit(unitType: .day, number: 1)?.isOnTheSameDateAs(date: date) == true || self
              .isOnTheSameDateAs(date: date) else { return false }
        var dateComponents = Calendar.current.dateComponents([.day, .month, .year, .hour], from: date)
        dateComponents.hour = 13

        guard let checkInDate = Calendar.current.date(from: dateComponents) else { return false }

        return 0...secondsInDay ~= checkInDate.timeIntervalSince(self)
    }

    // MARK: - Static

    static func dateWith(
        hour: Int,
        minute: Int = 0,
        second: Int = 0,
        year: Int? = nil,
        calendar: Calendar = Calendar.current
    ) -> Date? {
        var components = DateComponents()
        components.calendar = calendar
        components.year = year
        components.hour = hour
        components.minute = minute
        components.second = second

        return components.date
    }

    static func dateWith(
        hour: Int,
        year: Int? = nil,
        month: Int = 1,
        day: Int = 1,
        calendar: Calendar = Calendar.current
    ) -> Date? {
        var components = DateComponents()
        components.calendar = calendar
        components.year = year
        components.month = month
        components.day = day
        components.hour = hour
        components.minute = 0
        components.second = 0

        return components.date
    }

    static func dateWith(year: Int?, month: Int, day: Int, calendar: Calendar = Calendar.current) -> Date? {
        var components = DateComponents()
        components.calendar = calendar
        components.year = year
        components.month = month
        components.day = day

        return components.date
    }

    static func dateWith(month: Int, year: Int?, calendar: Calendar = Calendar.current) -> Date? {
        var components = DateComponents()
        components.calendar = calendar
        components.month = month
        components.year = year

        return components.date
    }

    static var localizedMediumDateStringForToday: String {
        Date().localizedDateString(.medium, date: Date())
    }

    private func localizedDateString(_ style: DateFormatter.Style, date: Date) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.locale = Locale.current
        dateFormatter.dateStyle = style

        return dateFormatter.string(from: date)
    }

    static func accFullDateSpoken(date: Date) -> String {
        let spokenDate = DateFormatter.localizedString(from: date, dateStyle: .full, timeStyle: .none)

        return spokenDate
    }

    static func englishLocaleDateString(date: Date) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.locale = Locale(identifier: "en_GB")
        dateFormatter.dateStyle = .full
        return dateFormatter.string(from: date)
    }
}

extension DateFormatter {
    static let slashDayMonthYearStringFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.setLocalizedDateFormatFromTemplate("dd/MM/yyyy")

        return formatter
    }()

    static let regCardGuestOutputFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter
    }()

    static let mediumDayMonthStringFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.setLocalizedDateFormatFromTemplate("EEE dd MMM")

        return formatter
    }()

    static let shortDayMonthStringFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.setLocalizedDateFormatFromTemplate("dd MMM")

        return formatter
    }()

    static let shortDayMonthYearStringFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.setLocalizedDateFormatFromTemplate("dd MMM, yy")

        return formatter
    }()

    static let mediumDayMonthYearStringFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.setLocalizedDateFormatFromTemplate("d MMM yyyy")

        return formatter
    }()

    static let fullDateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.setLocalizedDateFormatFromTemplate("EEEE d MMMM yyyy")

        return formatter
    }()

	static let analyticsDayFormatter: DateFormatter = {
		let formatter = DateFormatter()
		formatter.locale = Locale(identifier: "en_GB")
		formatter.dateFormat = "EE"

		return formatter
	}()

    static let analyticsTimeFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_GB")
        formatter.dateFormat = "HH:mm:ss"

        return formatter
    }()

    static let monthFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM"
        formatter.locale = Locale.current

        return formatter
    }()
}

extension Date {
    static func walletKeyFormat(arrival: Date?, departure: Date?) -> String? {
        guard let arrival,
              let departure else { return nil }

        let calendar = Calendar.current

        let startDay = calendar.component(.day, from: arrival)
        let startMonth = calendar.component(.month, from: arrival)
        let startYear = calendar.component(.year, from: arrival)

        let endDay = calendar.component(.day, from: departure)
        let endMonth = calendar.component(.month, from: departure)
        let endYear = calendar.component(.year, from: departure)

        let startMonthString = DateFormatter.monthFormatter.string(from: arrival)
        let endMonthString = DateFormatter.monthFormatter.string(from: departure)

        // Case 1: Same month & year
        if startMonth == endMonth && startYear == endYear {
            return String(
                format: "%02d - %02d %@ %d",
                startDay,
                endDay,
                startMonthString,
                startYear
            )
        }

        // Case 2: Same year, different month
        if startYear == endYear {
            return String(
                format: "%02d %@ - %02d %@ %d",
                startDay,
                startMonthString,
                endDay,
                endMonthString,
                startYear
            )
        }

        // Case 3: Different year
        return String(
            format: "%02d %@ %d - %02d %@ %d",
            startDay,
            startMonthString,
            startYear,
            endDay,
            endMonthString,
            endYear
        )
    }
}
