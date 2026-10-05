//
//  AmendRoomsTests.swift
//  SimpleNetworkTests
//
//  Created by Santa Gurung on 07/08/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class AmendRoomsTests: XCTestCase {

    var amendRoomCriteria: AmendRoomCriteria!
    var removeRoomCriteria: AmendRoomCriteria!

    override func setUp() {

        let leadGuest = LeadGuest(
            title: "Mr",
            firstName: "New",
            lastName: "Person",
            emailAddress: "newperson@foo.com"
        )

        let roomOccupancyAmend = RoomOccupancyAmend(
            adultsNumber: 1,
            childrenNumber: 0,
            cotRequired: false
        )

        amendRoomCriteria = AmendRoomCriteria(
            tempBookingRef: "AJK-1a61e9d8-dac0-47a9-95e6-67f97dff43fd",
            roomOccupancy: roomOccupancyAmend,
            leadGuest: leadGuest,
            roomType: "DB",
            token: "M9CA0KzmmwyRZCjfWII/72bHsLup1HkBwzwc9EcgU2K468A23rXCnDJO43OomdG1oG5Zoqc6eNoMrMQdMH1F0ynOPEhcAF94zyPlQgZQVQ0IOps=",
            reservationId: "mock-id",
            isBusiness: false,
            specialRequests: ["SING"]
        )

        removeRoomCriteria = AmendRoomCriteria(
            tempBookingRef: "AJK-1a61e9d8-dac0-47a9-95e6-67f97dff43fd",
            roomOccupancy: nil,
            leadGuest: nil,
            roomType: nil,
            token: "M9CA0KzmmwyRZCjfWII/72bHsLup1HkBwzwc9EcgU2K468A23rXCnDJO43OomdG1oG5Zoqc6eNoMrMQdMH1F0ynOPEhcAF94zyPlQgZQVQ0IOps=",
            reservationId: "mock-id",
            isBusiness: false,
            specialRequests: nil
        )
    }

    func testVariablesAreValidForAddRoom() {
        let mainDict = try! GraphQL.addNewRoomVariables(roomCriteria: amendRoomCriteria)
        let variables = mainDict["addNewRoomCriteria"] as! PIDictionary

        XCTAssertEqual(variables["tempBookingRef"] as! String, "AJK-1a61e9d8-dac0-47a9-95e6-67f97dff43fd")
        XCTAssertEqual(variables["token"] as! String, "M9CA0KzmmwyRZCjfWII/72bHsLup1HkBwzwc9EcgU2K468A23rXCnDJO43OomdG1oG5Zoqc6eNoMrMQdMH1F0ynOPEhcAF94zyPlQgZQVQ0IOps=")
        XCTAssertEqual(variables["roomType"] as! String, "DB")
        XCTAssertEqual(variables["specialRequests"] as! [String], ["SING"])

        let leadGuest = variables["leadGuest"] as! PIDictionary
        XCTAssertEqual(leadGuest["title"] as! String, "Mr")
        XCTAssertEqual(leadGuest["firstName"] as! String, "New")
        XCTAssertEqual(leadGuest["lastName"] as! String, "Person")
        XCTAssertEqual(leadGuest["emailAddress"] as! String, "newperson@foo.com")

        let roomOccupancyAmend = variables["roomOccupancy"] as! PIDictionary
        XCTAssertEqual(roomOccupancyAmend["adultsNumber"] as! Int, 1)
        XCTAssertEqual(roomOccupancyAmend["childrenNumber"] as! Int, 0)
        XCTAssertEqual(roomOccupancyAmend["cotRequired"] as! Bool, false)
    }

    func testVariablesAreValidForRemoveRoom() {
        let mainDict = try! GraphQL.removeRoomVariables(roomCriteria: removeRoomCriteria)

        XCTAssertEqual(mainDict["tempBookingRef"] as! String, "AJK-1a61e9d8-dac0-47a9-95e6-67f97dff43fd")
        XCTAssertEqual(mainDict["token"] as! String, "M9CA0KzmmwyRZCjfWII%2F72bHsLup1HkBwzwc9EcgU2K468A23rXCnDJO43OomdG1oG5Zoqc6eNoMrMQdMH1F0ynOPEhcAF94zyPlQgZQVQ0IOps%3D")
        XCTAssertEqual(mainDict["reservationId"] as! String, "mock-id")
        let bookingChannel = mainDict["bookingChannel"] as! PIDictionary
        XCTAssertEqual(bookingChannel["channel"] as! String, "PI")
    }

    func testVariablesAreValidForEditRoom() {
        let mainDict = try! GraphQL.amendEditRoomParameters(amendEdit: amendRoomCriteria)
        let variables = mainDict["editRoomCriteria"] as! PIDictionary

        XCTAssertEqual(variables["tempBookingRef"] as! String, "AJK-1a61e9d8-dac0-47a9-95e6-67f97dff43fd")
        XCTAssertEqual(variables["token"] as! String, "M9CA0KzmmwyRZCjfWII/72bHsLup1HkBwzwc9EcgU2K468A23rXCnDJO43OomdG1oG5Zoqc6eNoMrMQdMH1F0ynOPEhcAF94zyPlQgZQVQ0IOps=")
        XCTAssertEqual(variables["roomType"] as! String, "DB")
        XCTAssertEqual(variables["reservationId"] as! String, "mock-id")
        let bookingChannel = variables["bookingChannel"] as! PIDictionary
        XCTAssertEqual(bookingChannel["channel"] as! String, "PI")

        let leadGuest = variables["leadGuest"] as! PIDictionary
        XCTAssertEqual(leadGuest["title"] as! String, "Mr")
        XCTAssertEqual(leadGuest["firstName"] as! String, "New")
        XCTAssertEqual(leadGuest["lastName"] as! String, "Person")
        XCTAssertEqual(leadGuest["emailAddress"] as! String, "newperson@foo.com")

        let roomOccupancyAmend = variables["roomOccupancy"] as! PIDictionary
        XCTAssertEqual(roomOccupancyAmend["adultsNumber"] as! Int, 1)
        XCTAssertEqual(roomOccupancyAmend["childrenNumber"] as! Int, 0)
        XCTAssertEqual(roomOccupancyAmend["cotRequired"] as! Bool, false)
    }
}
