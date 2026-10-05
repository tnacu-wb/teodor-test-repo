//
//  SaveUpsellsRequestTest.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 07/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//
import XCTest
import Alamofire
@testable import SimpleNetwork

final class SaveUpsellsRequestTest: XCTestCase {

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    let webservice = Webservice.developmentGraphQL


    var mealDealUpsell: UpsellItem {
        return try! UpsellItem(dictionary: [
        "operaId": "MDP",
        "code": "17",
        "legend": "Meal Deal",
        "foodUpsell": true,
        "freeBreakfastTrigger": true,
        "freeBreakfastMaxPerMeal": 2,
        "freeBreakfastCode": "BFCHDF",
        "price": ["amount": "26.49",
                  "currency": "GBP"]
    ])
    }
    var continentalUpsell: UpsellItem {
        return try! UpsellItem(dictionary: [
        "operaId": "BFADCT",
        "code": "12",
        "legend": "Continental Breakfast",
        "foodUpsell": true,
        "freeBreakfastTrigger": false,
        "price": ["amount": "9.99",
                  "currency": "GBP"]
    ])
    }

    func testSaveUpsells() {

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
        bookingDetails.roomLettings = [room,room]
        bookingDetails.criteria.rooms = [room,room]

        bookingDetails.roomMealCombos = [RoomMealCombo(0,continentalUpsell,1),RoomMealCombo(0,mealDealUpsell,1),RoomMealCombo(1,mealDealUpsell,2)]
        // Valid resource
        do {
            let resource = try webservice.saveUpsellsToBooking(bookingDetails: bookingDetails)

            XCTAssertNotNil(resource)

            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])
            let variables = resource.parameters?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)
            let ancillariesCriteria = variables?["ancillariesCriteria"] as? PIDictionary
            XCTAssertNotNil(ancillariesCriteria)
            let roomsSelections = ancillariesCriteria?["roomsSelections"] as? [PIDictionary]
            XCTAssertNotNil(roomsSelections)
            XCTAssertEqual(roomsSelections?.count, 2)
            let aroomsSelection = roomsSelections?.first
            XCTAssertNotNil(aroomsSelection)
            let packageSelection = aroomsSelection?["packagesSelection"] as? [PIDictionary]
            let packageSelectionFirst = packageSelection?.first
            XCTAssertEqual(packageSelectionFirst?["noOfSelections"] as? Int, 1)
            XCTAssertEqual(packageSelectionFirst?["id"] as? String, "BFADCT")
            let packageSelectionSecond = packageSelection![1]
            XCTAssertEqual(packageSelectionSecond["noOfSelections"] as? Int, 1)
            XCTAssertEqual(packageSelectionSecond["id"] as? String, "MDP")
            let packageSelectionThird = packageSelection![2]
            XCTAssertEqual(packageSelectionThird["noOfSelections"] as? Int, 2)
            XCTAssertEqual(packageSelectionThird["id"] as? String, "BFCHDF")

        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }

}

