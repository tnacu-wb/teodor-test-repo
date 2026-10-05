//
//  SimpleCalendarTests.swift
//  SimpleCalendarTests
//
//  Created by Marcello Mascia on 26/07/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleCalendar

class SimpleCalendarTests: XCTestCase {

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    func testCalendarViewContoller() {

        let colors = SimpleCalendarSettings.Colors(
            weekday: .red,
            weekend: .red,
            selectable: .red,
            highlighted: .red,
            highlightedBackground: .red,
            selected: .red,
            notSelectable: .red,
            footer: .red,
            month: .red,
            today: .red
        )
        let fonts = SimpleCalendarSettings.Fonts(
            weekdays: .systemFont(ofSize: 16),
            days: .systemFont(ofSize: 15),
            footer: .systemFont(ofSize: 14),
            month: .systemFont(ofSize: 20)
        )
        let startDate = Date() - (60 * 60 * 24 * 40) // Today - 40 days
        let selectedDate = Date() + (60 * 60 * 24 * 3) // Today + 3 days

        let settings = SimpleCalendarSettings(colors: colors,
                                        fonts: fonts,
                                        selectedDate: selectedDate,
                                        startDate: startDate,
                                        selectableDatesOffset: 100,
                                        monthSpan: 19,
                                        freeSelection: true,
                                        maxDepartureDateCount: 364)

        let controller = CalendarViewController(settings: settings)
        controller.viewDidLoad()
        XCTAssertEqual(controller.title, NSLocalizedString("Select a date", comment: "Calendar view controller title"))
    }

    func testGetCalendarMonths() {

        let test1 = CalendarViewModel.calendarMonthsUsing(startDate: Date(), months: 12, selectableOffset: nil, maxDepartureDateCount: 364)
        XCTAssertEqual(test1.count, 12)

        let test2 = CalendarViewModel.calendarMonthsUsing(startDate: Date(), months: 1, selectableOffset: nil, maxDepartureDateCount: 364)
        XCTAssertEqual(test2.count, 1)

        let test3 = CalendarViewModel.calendarMonthsUsing(startDate: Date(), months: 0, selectableOffset: nil, maxDepartureDateCount: 364)
        XCTAssertEqual(test3.count, 0)
    }

    func testSameDayCheck() {

        var dateComponents = (Calendar.current as NSCalendar).components([.day, .month, .year], from: Date())
        dateComponents.hour = 3
        let testDate1 = Calendar.current.date(from: dateComponents)
        dateComponents.hour = 5
        let testDate2 = Calendar.current.date(from: dateComponents)

        XCTAssert(testDate1!.isOnTheSameDateAs(date: testDate2!), "Failed date is same day comparison")
    }

    func testDateIsInPast() {

        var dateComponents = (Calendar.current as NSCalendar).components([.day, .month, .year], from: Date())
        dateComponents.year = (dateComponents.year ?? 0) - 1

        if let date = Calendar.current.date(from: dateComponents) {

            XCTAssert(date.isInThePast, "Failed date is in past check")
        }

        if let date2 = (Calendar.current as NSCalendar).date(byAdding: .day, value: 1, to: Date(), options: NSCalendar.Options(rawValue: 0)) {

            XCTAssertFalse(date2.isInThePast, "Failed date is in past check with future date")
        }
    }

    func testCalendarDay() {

        let day = CalendarDay(day: 1, date: Date(), isSelectable: true)

        XCTAssertEqual(day.title, "1")
        XCTAssertEqual(day, day)
        XCTAssertNil(CalendarDay(day: 0, date: Date(), isSelectable: true).title)
    }

    func testCalendarMonth() {

        let month = CalendarMonth(title: "Giuglio", days: [])
        XCTAssertEqual(month, month)
    }
}
