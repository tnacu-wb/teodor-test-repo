//
//  AvailabilitiesTests.swift
//  SimpleNetworkTests
//
//  Created by Santa Gurung on 06/02/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class AvailabilitiesTests: XCTestCase {

    var bookingDetails: BookingDetails!

    override func setUp() {
        bookingDetails = BookingDetails()
    }

    override func tearDown() {
        bookingDetails = nil
    }
    
    func testVariablesAreValid() {
        bookingDetails.criteria = getCriteria()

        let placeDictionary: PIDictionary = ["suggestion": "London, UK", "placeId": "ChIJdd4hrwug2EcRmSrV3Vo6llI"]
        let suggestion = PISuggestion(placeDictionary: placeDictionary)!
        
        let variables = GraphQL.getAvalabilitiesVariables(bookingDetails: bookingDetails, suggestion: suggestion, page: 1, size: 1, sorting: .distance, allowEmployeeOffer: true)
        XCTAssertNotNil(variables["availabilitiesSearchCriteria"])

        let availabilitiesSearchCriteria = variables["availabilitiesSearchCriteria"] as! PIDictionary
        XCTAssertEqual(availabilitiesSearchCriteria["startDate"] as! String, "2050-09-12")
        XCTAssertEqual(availabilitiesSearchCriteria["endDate"] as! String, "2050-09-14")
        XCTAssertEqual(availabilitiesSearchCriteria["page"] as! Int, 1)
        XCTAssertEqual(availabilitiesSearchCriteria["initialPageSize"] as! Int, 1)
        XCTAssertEqual(availabilitiesSearchCriteria["lazyLoadPageSize"] as! Int, 1)
        XCTAssertEqual(availabilitiesSearchCriteria["sort"] as! String, "DISTANCE")

        let place = availabilitiesSearchCriteria["place"] as! PIDictionary
        XCTAssertEqual(place["location"] as! String, "ChIJdd4hrwug2EcRmSrV3Vo6llI")
        XCTAssertEqual(place["locationFormat"] as! String, "PLACEID")

        let rooms = availabilitiesSearchCriteria["rooms"] as! [PIDictionary]
        XCTAssertEqual(rooms.count, 1)
        let room = rooms.first!
        XCTAssertEqual(room["type"] as! String, "DB")
        XCTAssertEqual(room["adultsNumber"] as! Int, 2)
        XCTAssertEqual(room["childrenNumber"] as! Int, 0)
    }

    func getCriteria() -> Criteria {
        var criteria = Criteria()
        criteria.rooms = {
            let room = Room()
            room.adults = 2
            room.children = 0
            room.cotRequired = false
            room.type = RoomType.double
            return [room]
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
}
