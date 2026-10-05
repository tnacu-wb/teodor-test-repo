//
//  BookingHistoryMapperTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 07/07/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

class BookingHistoryMapperTest: XCTestCase {
    var sut = PIDictionary()
    var stay: Stay? = nil

override func setUp() {
    let fileURL = Bundle.module.url(forResource: "graphQLBookingHistory", withExtension: "json")!
    let data = try! Data(contentsOf: fileURL)
    let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
    let dataDictionary = jsonDictionary["data"] as! PIDictionary
    let getBookingHistory = dataDictionary["bookingHistory"] as! PIDictionary
    let bookingsDictionary = getBookingHistory["bookings"] as! [PIDictionary]

    sut = StayMapper.map(input: bookingsDictionary.first!, isBusiness: false)
    stay = try! Stay(dictionary: sut)
}

    func testStay() {

        XCTAssertEqual(stay?.lastName, "AIK")
        XCTAssertEqual(stay?.hotelCode, "LONLEI")
        XCTAssertEqual(stay?.hotelName, "London Leicester Square")
        XCTAssertEqual(stay?.identifier, "BAKR213510")
        XCTAssertEqual(stay?.arrivalDateString, "2023-09-18")
        XCTAssertEqual(stay?.checkOutDateString, "2023-09-19")
        XCTAssertEqual(stay?.cancelled, false)
        XCTAssertEqual(stay?.totalCost?.amount, 265.99)
        XCTAssertEqual(stay?.isCheckInOnlineAvailable, false)
        XCTAssertEqual(stay?.isCheckOutOnlineAvailable, false)
        XCTAssertEqual(stay?.basketStatus?.rawValue, "PRE_CHECKED_IN")
    }
}
