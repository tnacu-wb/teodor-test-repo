//
//  RequestsManagerTests.swift
//  PremierInnTests
//
//  Created by Louis Faria-Softly on 19/09/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

final class RequestsManagerTests: XCTestCase {

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testExample() throws {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        // Any test you write for XCTest can be annotated as throws and async.
        // Mark your test throws to produce an unexpected failure when your test encounters an uncaught error.
        // Mark your test async to allow awaiting for asynchronous code to complete. Check the results with assertions afterwards.
    }

    func testPerformanceExample() throws {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }

    var stayAccount: Stay {

        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "STUAIR"
        dictionary["hotelName"] = "Alpha2 London Blackfriars (Fleet Street)"
        dictionary["hotelLatitude"] = 50.823071
        dictionary["hotelLongitude"] = -0.140976
        dictionary["importType"] = "account"
        dictionary["lastName"] = "APPS"
        dictionary["identifier"] = "BBER264250"
        dictionary["arrivalDate"] = "2017-10-09"
        dictionary["checkOutDate"] = "2017-10-10"
        dictionary["imageURL"] = "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"

        return try! Stay(dictionary: dictionary)
    }
    var stayImported: Stay {

        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "STUAIR"
        dictionary["hotelName"] = "Alpha2 London Blackfriars (Fleet Street)"
        dictionary["hotelLatitude"] = 50.823071
        dictionary["hotelLongitude"] = -0.140976
        dictionary["importType"] = "imported"
        dictionary["lastName"] = "APPS"
        dictionary["identifier"] = "BBER264250"
        dictionary["arrivalDate"] = "2017-10-09"
        dictionary["checkOutDate"] = "2017-10-10"
        dictionary["imageURL"] = "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"

        return try! Stay(dictionary: dictionary)
    }

    func testUserLogoutRemoveImported() {
        let reservationsManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)
        _ = reservationsManager.update(with: [stayAccount, stayImported])

        let requestManager = RequestsManager.self
        requestManager.removeStays()

        XCTAssert(reservationsManager.items.count == 0)
    }

    func testUserLogoutRemoveOnlyAccount() {
        let reservationsManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)
        _ = reservationsManager.update(with: [stayAccount, stayImported])

        let requestManager = RequestsManager.self
        requestManager.removeStays(accountOnly: true)

        XCTAssert(reservationsManager.items.count == 1)
    }

}
