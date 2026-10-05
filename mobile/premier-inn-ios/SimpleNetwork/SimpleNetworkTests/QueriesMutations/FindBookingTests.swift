//
//  FindBookingTests.swift
//  SimpleNetworkTests
//
//  Created by Santa Gurung on 28/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class FindBookingTests: XCTestCase {

    func testFindBookingSourceDecoding() {
        let data = getDataFor(fileName: "graphQLFindBooking")

        do {
            let bookingSource = try JSONDecoder().decode(FindBookingSource.self, from: data)
            XCTAssertEqual(bookingSource.sourcePms, .bart)
            XCTAssertEqual(bookingSource.bookingReference, "AWM5020097")
            XCTAssertEqual(bookingSource.token, "<encryt_token>")
            XCTAssertEqual(bookingSource.hotelId, "hotelId")
        } catch {
            XCTFail("Decoding failed: \(error)")
        }
    }

    func testFindBookingVariablesAreValid() {
        let arrivalDate: Date = {
            var dateComponents = DateComponents()
            dateComponents.year = 2050
            dateComponents.month = 9
            dateComponents.day = 12
            return Calendar.current.date(from: dateComponents)!
        }()

        let findBookingDetails: FindBookingDetails = (reservationId: "AWM5020097", surname: "Gurungwb", arrivalDate: arrivalDate, business: false)
        let variables = GraphQL.findBookingSourceVariables(findBookingDetails: findBookingDetails)
        let findBookingCriteriaDict = variables["findBookingCriteria"] as! PIDictionary

        XCTAssertNotNil(findBookingCriteriaDict, "findBookingCriteria should have some values")
        XCTAssertEqual(findBookingCriteriaDict["resNo"] as! String, "AWM5020097")
        XCTAssertEqual(findBookingCriteriaDict["lastName"] as! String, "Gurungwb")
        XCTAssertEqual(findBookingCriteriaDict["arrivalDate"] as! String, "2050-09-12")
    }

    private func getDataFor(fileName: String) -> Data {
        let fileURL = Bundle.module.url(forResource: fileName, withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let findBookingDictionary = dataDictionary["findBooking"] as! PIDictionary
        return try! JSONSerialization.data(withJSONObject: findBookingDictionary)
    }
}
