//
//  AnnouncementExtensionsTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Georgios Aikaterinakis on 21/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

@testable import PremierInn

class AnnouncementExtensionsTests: XCTestCase {

    func do_testAnnouncement(showAnnouncement: Bool, text: String?, startDateString: String?, endDateString: String?, arrivalDateString: String, departureDateString: String?, expectToShow: Bool, testName: String) {

        let announcement = Announcement(showAnnouncement: showAnnouncement, text: text, startDateString: startDateString, endDateString: endDateString)

        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd/MM/yyyy"

        let arrivalDate = dateFormatter.date(from: arrivalDateString) ?? Date(timeIntervalSinceNow: 0)

        var departureDate: Date?
        if let departureDateString = departureDateString {
            departureDate = dateFormatter.date(from: departureDateString)
        }

        let announcementToShow = announcement.announcementToShow(arrivalDate: arrivalDate, departureDate: departureDate)
        let announcementToShowExists = announcementToShow != nil

        XCTAssert(announcementToShowExists == expectToShow, testName)
    }

    func testAnnouncements() {

        // text related tests
        do_testAnnouncement(showAnnouncement: false, text: nil, startDateString: nil, endDateString: nil, arrivalDateString: "01/03/2025", departureDateString: nil, expectToShow: false, testName: "showAnnouncement: false, text: nil")
        do_testAnnouncement(showAnnouncement: true, text: nil, startDateString: nil, endDateString: nil, arrivalDateString: "01/03/2025", departureDateString: nil, expectToShow: false, testName: "showAnnouncement: true, text: nil")
        do_testAnnouncement(showAnnouncement: true, text: "", startDateString: nil, endDateString: nil, arrivalDateString: "01/03/2025", departureDateString: nil, expectToShow: false, testName: "showAnnouncement: true, text: \"\"")

        // no dates restriction - happy paths
        do_testAnnouncement(showAnnouncement: true, text: "an announcement", startDateString: nil, endDateString: nil, arrivalDateString: "01/03/2025", departureDateString: nil, expectToShow: true, testName: "happy path")
        do_testAnnouncement(showAnnouncement: true, text: "an announcement", startDateString: "05/03/2025", endDateString: nil, arrivalDateString: "01/03/2025", departureDateString: nil, expectToShow: true, testName: "happy path with startDate only")
        do_testAnnouncement(showAnnouncement: true, text: "an announcement", startDateString: nil, endDateString: "10/03/2025", arrivalDateString: "01/03/2025", departureDateString: nil, expectToShow: true, testName: "happy path with endDate only")

        // with dates restriction
        do_testAnnouncement(showAnnouncement: true, text: "an announcement", startDateString: "15/03/2025", endDateString: "20/03/2025", arrivalDateString: "01/03/2025", departureDateString: nil, expectToShow: false, testName: "no departureDate")
        do_testAnnouncement(showAnnouncement: true, text: "an announcement", startDateString: "15/03/2025", endDateString: "20/03/2025", arrivalDateString: "01/03/2025", departureDateString: "10/03/2025", expectToShow: false, testName: "booking dates before restriction")
        do_testAnnouncement(showAnnouncement: true, text: "an announcement", startDateString: "15/03/2025", endDateString: "20/03/2025", arrivalDateString: "01/03/2025", departureDateString: "15/03/2025", expectToShow: true, testName: "departureDate same as startDate")
        do_testAnnouncement(showAnnouncement: true, text: "an announcement", startDateString: "15/03/2025", endDateString: "20/03/2025", arrivalDateString: "01/03/2025", departureDateString: "16/03/2025", expectToShow: true, testName: "departureDate inside date restriction range")
        do_testAnnouncement(showAnnouncement: true, text: "an announcement", startDateString: "15/03/2025", endDateString: "20/03/2025", arrivalDateString: "01/03/2025", departureDateString: "21/03/2025", expectToShow: true, testName: "booking dates overlap the whole date restriction range")
        do_testAnnouncement(showAnnouncement: true, text: "an announcement", startDateString: "15/03/2025", endDateString: "20/03/2025", arrivalDateString: "16/03/2025", departureDateString: "17/03/2025", expectToShow: true, testName: "booking dates inside date restriction range")
        do_testAnnouncement(showAnnouncement: true, text: "an announcement", startDateString: "15/03/2025", endDateString: "20/03/2025", arrivalDateString: "16/03/2025", departureDateString: "21/03/2025", expectToShow: true, testName: "arrivalDate inside date restriction range, departureDate after endDate")
        do_testAnnouncement(showAnnouncement: true, text: "an announcement", startDateString: "15/03/2025", endDateString: "20/03/2025", arrivalDateString: "20/03/2025", departureDateString: "21/03/2025", expectToShow: true, testName: "arrivalDate same as endDate")
        do_testAnnouncement(showAnnouncement: true, text: "an announcement", startDateString: "15/03/2025", endDateString: "20/03/2025", arrivalDateString: "21/03/2025", departureDateString: "25/03/2025", expectToShow: false, testName: "booking dates after restriction")
    }
}
