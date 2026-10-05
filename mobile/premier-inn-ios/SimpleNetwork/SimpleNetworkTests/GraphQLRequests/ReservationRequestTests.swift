//
//  ReservationRequestTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 14/11/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
import Alamofire
@testable import SimpleNetwork

final class ReservationRequestTests: XCTestCase {

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }
    let webservice = Webservice.developmentGraphQL

    func testReservationWithManageBooking() {

        let arrivalDate: Date = {
                var dateComponents = DateComponents()
                dateComponents.year = 2050
                dateComponents.month = 9
                dateComponents.day = 12
                dateComponents.hour = 12
                dateComponents.timeZone = TimeZone(identifier: "Europe/London")

                return Calendar.current.date(from: dateComponents)!
            }()

        let reservationDetails = ReservationDetails(reservationId: "mock-id", surname: "McBooky", arrivalDate: arrivalDate, business: false, token: "test-token")

        // Valid resource
        do {
            let resource = try webservice.reservation(reservationDetails: reservationDetails, hotelCode: "BRIPTI", bookingDetails: nil)
            XCTAssertNotNil(resource)

            // Validate query
            let query = resource.parameters?["query"] as? String
            XCTAssertEqual(query!, GraphQL.bookingConfirmationWithManageBookingQuery)

            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])
            let variables = resource.parameters?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)
            XCTAssertNotNil(variables?["basketReference"])
            XCTAssertNotNil(variables?["country"])
            XCTAssertNotNil(variables?["language"])

            let cancelInformationCriteria = variables?["cancelInformationCriteria"] as? PIDictionary
            XCTAssertNotNil(cancelInformationCriteria?["userDateTime"])
            XCTAssertNotNil(cancelInformationCriteria?["hotelId"])
            XCTAssertNotNil(cancelInformationCriteria?["basketReference"])
            XCTAssertNotNil(cancelInformationCriteria?["token"])

        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }

    func testReservationStandAlone() {

        let arrivalDate: Date = {
                var dateComponents = DateComponents()
                dateComponents.year = 2050
                dateComponents.month = 9
                dateComponents.day = 12
                dateComponents.hour = 12
                dateComponents.timeZone = TimeZone(identifier: "Europe/London")

                return Calendar.current.date(from: dateComponents)!
            }()

        let reservationDetails = ReservationDetails(reservationId: "mock-id", surname: "McBooky", arrivalDate: arrivalDate, business: false, token: "test-token")

        // Valid resource
        do {
            let resource = try webservice.reservation(reservationDetails: reservationDetails, hotelCode: nil, bookingDetails: nil)
            XCTAssertNotNil(resource)

            // Validate query
            let query = resource.parameters?["query"] as? String
            XCTAssertEqual(query!, GraphQL.bookingConfirmationOnlyQuery)

            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])
            let variables = resource.parameters?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)
            XCTAssertNotNil(variables?["basketReference"])
            XCTAssertNotNil(variables?["country"])
            XCTAssertNotNil(variables?["language"])

        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }
}
