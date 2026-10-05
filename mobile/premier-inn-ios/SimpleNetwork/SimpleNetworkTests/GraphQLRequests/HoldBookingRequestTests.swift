//
//  HoldBookingRequestTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 14/11/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
import Alamofire
@testable import SimpleNetwork

final class HoldBookingRequestTests: XCTestCase {

    let webservice = Webservice.developmentGraphQL

    func testHoldBooking() {

        // hotelCode missing
        XCTAssertThrowsError(try webservice.holdBooking(bookingDetails: BookingDetails.sharedInstance, sensorData: ""))

        let bookingDetails = BookingDetails()
        let hotel = try? Hotel(dictionary: [
            "hotelCode": "BRIPTI",
            "hotelInfo": [
                "name": "PremierInn Brighton",
                "address": ["postcode": "BN1 1RE", "addressline1": "144 North Street", "addressline2": "Brighton", "addressline3": "East Sussex", "country": "United Kingdom (the)"]
            ]])
        bookingDetails.hotel = hotel

        var criteria: Criteria {
            var criteria = Criteria()
            criteria.arrivalDate = {
                var dateComponents = DateComponents()
                dateComponents.year = 2050
                dateComponents.month = 9
                dateComponents.day = 12
                dateComponents.hour = 12
                dateComponents.timeZone = TimeZone(identifier: "Europe/London")

                return Calendar.current.date(from: dateComponents)!
            }()
            criteria.nights = 2

            return criteria
        }
        bookingDetails.criteria = criteria

        let rate = Rate(
            uniqueID: UUID(),
            isBiggerRoom: false,
            code: "FLEXRATE",
            totalCost: Cost(amount: 00.00, currencyCode: "GBP"),
            description: nil,
            name: "FLEXRATE",
            text: nil,
            classification: "FLEXRATE",
            rooms: nil,
            upsellItems: nil,
            cellCode: .none,
            promotionCode: nil,
            lettingTypes: []
        )
        bookingDetails.rate = rate

        var option = RoomLettingOption()
        option.lettingType = "FAM"
        option.totalCost = Cost(amount: 12, currencyCode: "GBP")

        let room = Room()
        room.adults = 2
        room.children = 2
        room.cotRequired = false
        room.options = [option]
        bookingDetails.roomLettings = [room]
        // Valid resource
        do {
            let resource = try webservice.holdBooking(bookingDetails: bookingDetails, sensorData: "")

            XCTAssertNotNil(resource)

            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])
            let variables = resource.parameters?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)
            let createReservationCriteria = variables?["createReservationCriteria"] as? PIDictionary
            XCTAssertNotNil(createReservationCriteria)
            let reservations = createReservationCriteria?["reservations"] as? [PIDictionary]
            XCTAssertNotNil(reservations)
            XCTAssertEqual(reservations?.count, 1)
            let aReservation = reservations?.first
            XCTAssertNotNil(aReservation)
            XCTAssertEqual(aReservation?["hotelId"] as? String, "BRIPTI")
            XCTAssertEqual(aReservation?["arrival"] as? String, "2050-09-12")
            XCTAssertEqual(aReservation?["departure"] as? String, "2050-09-14")
            XCTAssertEqual(aReservation?["adultsNumber"] as? Int, 2)
            XCTAssertEqual(aReservation?["childrenNumber"] as? Int, 2)
            XCTAssertEqual(aReservation?["cotRequired"] as? Bool, false)
            let roomRates = aReservation?["roomRates"] as? PIDictionary
            XCTAssertNotNil(roomRates)
            XCTAssertEqual(roomRates?["startDate"] as? String, "2050-09-12")
            XCTAssertEqual(roomRates?["endDate"] as? String, "2050-09-14")
            XCTAssertEqual(roomRates?["pmsRoomType"] as? String, "FAM")
            XCTAssertEqual(roomRates?["ratePlanCode"] as? String, "FLEXRATE")

        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }

}
