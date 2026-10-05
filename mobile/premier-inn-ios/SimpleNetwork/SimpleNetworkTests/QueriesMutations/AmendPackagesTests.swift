//
//  AmendPackagesTests.swift
//  SimpleNetworkTests
//
//  Created by Santa Gurung on 26/07/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class AmendPackagesTests: XCTestCase {

    var amendPackagesDetail: AmendPackagesDetail!
    var ciolAmendInfo: CiolAmendInfo!
    let basketID = "BFJ-8f072212-84c4-4917-9d53-457e3136562a"
    let hotelID = "MATBAK"

    override func setUp() {

        // Test Data: Booking with 2 rooms
        let meal1 = try! UpsellItem(dictionary: ["code": "11", "legend": "", "adults": 1, "roomId": "162404", "operaId":"BFADBF", "foodUpsell": true, "price": ["amount": "10", "currency": "GBP"]])
        let meal2 = try! UpsellItem(dictionary: ["code": "12", "legend": "", "adults": 1, "roomId": "162403", "operaId":"BFADCT", "foodUpsell": true, "price": ["amount": "10", "currency": "GBP"]])

        let currentlySelectedPackagesByRooms = [
            meal1,
            meal2
        ]

        let previouslySelectedPackagesByRooms = [UpsellItem]()

        let arrivalDate = {
            var dateComponents = DateComponents()
            dateComponents.year = 2050
            dateComponents.month = 9
            dateComponents.day = 12
            dateComponents.hour = 12
            dateComponents.timeZone = TimeZone(identifier: "Europe/London")

            return Calendar.current.date(from: dateComponents)!
        }()

        let departureDate = {
            var dateComponents = DateComponents()
            dateComponents.year = 2050
            dateComponents.month = 9
            dateComponents.day = 14
            dateComponents.hour = 12
            dateComponents.timeZone = TimeZone(identifier: "Europe/London")

            return Calendar.current.date(from: dateComponents)!
        }()


        var rooms = [Room(dictionary: ["type": "DB", "substitutedType": false, "roomId":"162404"])]
        rooms.append(Room(dictionary: ["type": "DB", "substitutedType": false, "roomId":"162403"]))
        amendPackagesDetail = AmendPackagesDetail(
            basketReferenceId: basketID,
            hotelId: hotelID,
            arrivalDate: arrivalDate,
            departureDate: departureDate,
            roomsSelections: currentlySelectedPackagesByRooms,
            previousRoomsSelections: previouslySelectedPackagesByRooms,
            rooms: rooms
        )
        let mealPackage1 = CiolUpsellSelection(id: "BFADBF", noOfSelections: 1)
        let mealPackage2 = CiolUpsellSelection(id: "BFADCT", noOfSelections: 1)
        let roomSelection1 = CiolUpsellRoomSelection(reservationId: "162404", packagesSelection: [mealPackage1])
        let roomSelection2 = CiolUpsellRoomSelection(reservationId: "162403", packagesSelection: [mealPackage2])

        ciolAmendInfo = CiolAmendInfo(basketReferenceId: basketID, hotelId: hotelID, arrivalDate: arrivalDate, departureDate: departureDate, roomsSelections: [roomSelection1, roomSelection2], previousRoomsSelections: [])
    }

    func testVariablesAreValid() {
        let mainDict = GraphQL.amendPackagesParameters(parameter: amendPackagesDetail)
        let variables = mainDict["updateReservationPackagesRequest"] as! PIDictionary

        XCTAssertEqual(variables["basketReferenceId"] as! String, basketID)
        XCTAssertEqual(variables["hotelId"] as! String, hotelID)
        XCTAssertEqual(variables["arrivalDate"] as! String, "2050-09-12")
        XCTAssertEqual(variables["departureDate"] as! String, "2050-09-14")

        // Currently selected packages
        let currentlySelectedPackagesRoomOne = (variables["roomsSelections"] as! [PIDictionary]).first!
        XCTAssertEqual(currentlySelectedPackagesRoomOne["reservationId"] as! String, "162404")
        let firstPackageFromRoomOne = (currentlySelectedPackagesRoomOne["packagesSelection"] as! [PIDictionary]).first!
        XCTAssertEqual(firstPackageFromRoomOne["id"] as! String, "BFADBF")
        XCTAssertEqual(firstPackageFromRoomOne["noOfSelections"] as! Int, 1)

        // Previously selected packages
        let previouslySelectedPackagesRoomTwo = (variables["previousRoomsSelections"] as! [PIDictionary]).last!
        XCTAssertEqual(previouslySelectedPackagesRoomTwo["reservationId"] as! String, "162403")
        let packageFromRoomTwo = previouslySelectedPackagesRoomTwo["packagesSelection"] as! [PIDictionary]
        XCTAssertTrue(packageFromRoomTwo.isEmpty)
    }

    func testVariablesAreValidCiol() {
        let mainDict = GraphQL.amendCiolPackagesParameters(amendInfo: ciolAmendInfo)
        let variables = mainDict["updateReservationPackagesRequest"] as! PIDictionary

        XCTAssertEqual(variables["basketReferenceId"] as! String, basketID)
        XCTAssertEqual(variables["hotelId"] as! String, hotelID)
        XCTAssertEqual(variables["arrivalDate"] as! String, "2050-09-12")
        XCTAssertEqual(variables["departureDate"] as! String, "2050-09-14")

        // Currently selected packages
        let currentlySelectedPackagesRoomOne = (variables["roomsSelections"] as! [PIDictionary]).first!
        XCTAssertEqual(currentlySelectedPackagesRoomOne["reservationId"] as! String, "162404")
        let firstPackageFromRoomOne = (currentlySelectedPackagesRoomOne["packagesSelection"] as! [PIDictionary]).first!
        XCTAssertEqual(firstPackageFromRoomOne["id"] as! String, "BFADBF")
        XCTAssertEqual(firstPackageFromRoomOne["noOfSelections"] as! Int, 1)
    }
}
