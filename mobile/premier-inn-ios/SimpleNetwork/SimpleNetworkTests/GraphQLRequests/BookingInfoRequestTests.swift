//
//  BookingInfoRequestTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 21/08/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class BookingInfoRequestTests: XCTestCase {

    let webservice = Webservice.developmentGraphQL

    func testHeaderInformation() {

        do {
            let resource = try webservice.bookingInformation(basketReference: "Test-Ref", isBusiness: false)

            XCTAssertNotNil(resource)

            // Validate query
            let query = resource.parameters?["query"] as? String
            XCTAssertEqual(query!, GraphQL.bookingInformationOnlyQuery)
            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])
            let variables = resource.parameters?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)
            let basketRef = variables?["basketReference"] as? String
            
            XCTAssertEqual(basketRef, "Test-Ref")
           

        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }

    func testResendInvoiceEmail() {

        let reservation = try! Reservation(dictionary: BookingDetailsTests.reservationDictionary)

        do {
            let resource = try webservice.resendInvoiceEmail(reservation: reservation)

            XCTAssertNotNil(resource)

            // Validate query
            let query = resource.parameters?["query"] as? String
            XCTAssertEqual(query!, GraphQL.resendInvoiceEmailMutation)
            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])
            let variables = resource.parameters?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)
            let resendInvoiceRequest = variables?["resendInvoiceRequest"] as? PIDictionary

            let hotelId = resendInvoiceRequest?["hotelId"] as? String
            let bookingReference = resendInvoiceRequest?["bookingReference"] as? String
            let invoiceRecordNumber = resendInvoiceRequest?["invoiceRecordNumber"] as? String
            let bookingChannelDictionary = resendInvoiceRequest?["bookingChannel"] as? PIDictionary

            XCTAssertEqual(hotelId, "SOUANC")
            XCTAssertEqual(bookingReference, "abc-123-abc-123")
            XCTAssertEqual(invoiceRecordNumber, "0")
            XCTAssertNotNil(bookingChannelDictionary)
            XCTAssertEqual(bookingChannelDictionary?["channel"] as? String, "PI")
        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }
}
