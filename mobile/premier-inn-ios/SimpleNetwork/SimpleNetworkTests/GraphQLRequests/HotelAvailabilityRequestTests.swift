//
//  HotelAvailabilityRequestTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 14/11/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
import Alamofire
@testable import SimpleNetwork

final class HotelAvailabilityRequestTests: XCTestCase {

    let webservice = Webservice.developmentGraphQL

    func testHotelAvailability() {

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
            criteria.rooms = {
                let room1 = Room()
                room1.adults = 2
                room1.children = 2
                room1.cotRequired = true
                room1.type = RoomType.family

                let room2 = Room()
                room2.adults = 1
                room2.children = 0
                room2.type = RoomType.accessible

                return [room1, room2]
            }()
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
        bookingDetails.booker = try! User(title: "Mr", firstName: "Booker", lastName: "McBooky")
        // Valid resource
        do {
            let resource = try webservice.hotelAvailability(hotelCode: hotel!.code, hotelBrand: hotel!.brand, bookingDetails: bookingDetails, allowEmployeeOffer: true)
            XCTAssertNotNil(resource)

            // Validate query
            let query = resource.parameters?["query"] as? String
            XCTAssertEqual(query!, GraphQL.availabilityAndPackagesQuery)

            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])
            let variables = resource.parameters?["variables"] as? PIDictionary
            let availabilitySearchCriteria = variables?["availabilitySearchCriteria"] as? PIDictionary
            XCTAssertNotNil(availabilitySearchCriteria)
            XCTAssertEqual(availabilitySearchCriteria?["arrival"] as? String, "2050-09-12")
            XCTAssertEqual(availabilitySearchCriteria?["departure"] as? String, "2050-09-14")
            let rooms = availabilitySearchCriteria?["rooms"] as? [PIDictionary]
            XCTAssertNotNil(rooms)
        
        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }
}
