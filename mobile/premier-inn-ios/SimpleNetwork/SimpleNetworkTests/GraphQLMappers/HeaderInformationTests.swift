//
//  HeaderInformationTests.swift
//  SimpleNetworkTests
//
//  Created by Georgios Aikaterinakis on 29/07/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import XCTest

@testable import SimpleNetwork

class HeaderInformationTests: XCTestCase {

    var headerInformation: HeaderInformation? = nil

    override func setUp() {
        let fileURL = Bundle.module.url(forResource: "graphQLHeaderInformation", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let headerInformationDict = dataDictionary["headerInformation"] as! PIDictionary

        do {
            let headerInformationData = try JSONSerialization.data(withJSONObject: headerInformationDict, options: .prettyPrinted)

            let decoder = JSONDecoder()
            headerInformation = try decoder.decode(HeaderInformation.self, from: headerInformationData)
        } catch {
            XCTFail("error thrown when decoding HeaderInformation")
        }
    }

    func testHeaderInformation() {

        let content = headerInformation?.content
        let global = content?.global

        XCTAssertEqual(global?.addRoom, "Add another room")
        XCTAssertEqual(global?.done, "Done")
        XCTAssertEqual(global?.room, "room")
        XCTAssertEqual(global?.roomLabel, "Room")
        XCTAssertEqual(global?.single, "Single")
        XCTAssertEqual(global?.double, "Double")
        XCTAssertEqual(global?.twin, "Twin")
        XCTAssertEqual(global?.accessible, "Accessible")
        XCTAssertEqual(global?.family, "Family")
        XCTAssertEqual(global?.adult, "adult")
        XCTAssertEqual(global?.adults, "adults")
        XCTAssertEqual(global?.child, "child")
        XCTAssertEqual(global?.children, "children")
        XCTAssertEqual(global?.night, "night")
        XCTAssertEqual(global?.rooms, "rooms")
        XCTAssertEqual(global?.adultsLabel, "Adults")
        XCTAssertEqual(global?.childrenLabel, "Children")
        XCTAssertEqual(global?.today, "Today")
        XCTAssertEqual(global?.tomorrow, "Tomorrow")

        let menu = content?.menu

        XCTAssertEqual(menu?.mobileMenuButton, "Menu")
        XCTAssertEqual(menu?.language, "Language")
        XCTAssertEqual(menu?.business, "Business")
        XCTAssertEqual(menu?.languageButton, "English")
        XCTAssertEqual(menu?.tick, "/etc/clientlibs/pi-header/resources/images/tick.svg")
        XCTAssertEqual(menu?.logIn, "Log in")
        XCTAssertEqual(menu?.discoverPI, "Discover Premier Inn")
        XCTAssertEqual(menu?.findBooking, "Manage booking")
        XCTAssertEqual(menu?.bookHotel, "Search for a hotel")
        XCTAssertEqual(menu?.guestAccount, nil)
        XCTAssertEqual(menu?.changeLogs, nil)
        XCTAssertEqual(menu?.agentMemo, nil)

        let form = headerInformation?.form

        XCTAssertEqual(form?.childrenHelperText, "2-15 years")
        XCTAssertEqual(form?.adultsHelperText, "Max 2 per room")
        XCTAssertEqual(form?.includeCot, "Include a cot?")
        XCTAssertEqual(form?.cotLimit, "0-2 years")
        XCTAssertEqual(form?.removeRoom, "Remove room")
        XCTAssertEqual(form?.checkout, "Check out:")
        XCTAssertEqual(form?.roomType, "Room type")
        XCTAssertEqual(form?.whereEmailLandingPage, "Enter place or postcode")
        XCTAssertEqual(form?.whereString, "Enter place, postcode or hotel")

        let datePicker = headerInformation?.datePicker
        XCTAssertEqual(datePicker?.reset, "Reset")

        let results = headerInformation?.results
        let notifications = results?.notifications
        XCTAssertEqual(notifications?.groupBookingHeader, "Unable to add more rooms")
        XCTAssertEqual(notifications?.groupBookingMessage, "If you’d like to book five rooms or more, please call us and we’ll be happy to help.")

        let config = headerInformation?.config
        let roomCodes = config?.roomCodes
        XCTAssertEqual(roomCodes?.double, "DB")
        XCTAssertEqual(roomCodes?.family, "FAM")
        XCTAssertEqual(roomCodes?.accessible, "DIS")
        XCTAssertEqual(roomCodes?.single, "SB")
        XCTAssertEqual(roomCodes?.twin, "TWIN")
    }
}
