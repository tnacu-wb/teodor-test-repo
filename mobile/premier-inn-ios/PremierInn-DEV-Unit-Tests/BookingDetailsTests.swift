//
//  BookingDetailsTests.swift
//  PremierInnTests
//
//  Created by Freddie Parks on 19/01/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

class BookingDetailsTests: XCTestCase {

    override class func setUp() {
        super.setUp()
    }

    override class func tearDown() {
        super.tearDown()
    }

    var hotel: Hotel? {

        do {
            let fileURL = Bundle(for: type(of: self)).url(forResource: "hotelInfo", withExtension: "json")!
            let data = try Data(contentsOf: fileURL)
            guard let jsonDictionary = try JSONSerialization.jsonObject(with: data, options: .allowFragments) as? PIDictionary else { return nil }
            let hotel = try Hotel(dictionary:jsonDictionary)

            return hotel
        } catch {
            XCTFail("\(error)")
            return nil
        }
    }

    var roomLettings: [Room]? {

        let roomDictionary: PIDictionary = [
            "roomId": "lol",
            "options": [
                [
                    "lettingType": "DBS",
                    "totalCost": [
                        "amount": 99.99,
                        "currency": "USD"
                    ],
                    "cityTax": [
                        "amount": 2.00,
                        "currency": "USD"
                    ]
                ]
            ]
        ]

        let room = Room(dictionary: roomDictionary)

        return [room]
    }

    // MARK: City tax
    
    func testCityTaxForLeisure() {

        let bookingDetails = BookingDetails()
        bookingDetails.hotel = hotel
        bookingDetails.purpose = .leisure

        bookingDetails.hotel?.update(cityTaxResponse: (false, true))
        XCTAssert(bookingDetails.cityTaxRequired == false)

        bookingDetails.hotel?.update(cityTaxResponse: (false, false))
        XCTAssert(bookingDetails.cityTaxRequired == false)

        bookingDetails.hotel?.update(cityTaxResponse: (true, false))
        XCTAssert(bookingDetails.cityTaxRequired == true)

        bookingDetails.hotel?.update(cityTaxResponse: (true, true))
        XCTAssert(bookingDetails.cityTaxRequired == true)
    }

    func testCityTaxForBusiness() {

        let bookingDetails = BookingDetails()
        bookingDetails.hotel = hotel
        bookingDetails.purpose = .business

        bookingDetails.hotel?.update(cityTaxResponse: (true, false))
        XCTAssert(bookingDetails.cityTaxRequired == false)

        bookingDetails.hotel?.update(cityTaxResponse: (false, false))
        XCTAssert(bookingDetails.cityTaxRequired == false)

        bookingDetails.hotel?.update(cityTaxResponse: (false, true))
        XCTAssert(bookingDetails.cityTaxRequired == true)

        bookingDetails.hotel?.update(cityTaxResponse: (true, true))
        XCTAssert(bookingDetails.cityTaxRequired == true)
    }

    func testTotalCostForLeisure() {

        let bookingDetails = BookingDetails()
        bookingDetails.hotel = hotel
        bookingDetails.purpose = .leisure
        bookingDetails.roomLettings = roomLettings

        bookingDetails.hotel?.update(cityTaxResponse: (true, false))
        XCTAssertEqual(bookingDetails.roomCost?.amount, NSDecimalNumber(string: "99.99"))

        bookingDetails.hotel?.update(cityTaxResponse: (false, false))
        XCTAssertEqual(bookingDetails.roomCost?.amount, NSDecimalNumber(string: "97.99"))

        bookingDetails.hotel?.update(cityTaxResponse: (false, true))
        XCTAssertEqual(bookingDetails.roomCost?.amount, NSDecimalNumber(string: "97.99"))

        bookingDetails.hotel?.update(cityTaxResponse: (true, true))
        XCTAssertEqual(bookingDetails.roomCost?.amount, NSDecimalNumber(string: "99.99"))
    }

    func testTotalCostForBusiness() {

        let bookingDetails = BookingDetails()
        bookingDetails.hotel = hotel
        bookingDetails.purpose = .business
        bookingDetails.roomLettings = roomLettings

        bookingDetails.hotel?.update(cityTaxResponse: (true, true))
        XCTAssertEqual(bookingDetails.roomCost?.amount, NSDecimalNumber(string: "99.99"))

        bookingDetails.hotel?.update(cityTaxResponse: (true, false))
        XCTAssertEqual(bookingDetails.roomCost?.amount, NSDecimalNumber(string: "97.99"))

        bookingDetails.hotel?.update(cityTaxResponse: (false, false))
        XCTAssertEqual(bookingDetails.roomCost?.amount, NSDecimalNumber(string: "97.99"))

        bookingDetails.hotel?.update(cityTaxResponse: (false, true))
        XCTAssertEqual(bookingDetails.roomCost?.amount, NSDecimalNumber(string: "99.99"))
    }

    private let piBreakfast: PIDictionary = [
        "operaId": "BFADBF",
        "code": 11,
        "legend": "Premier Inn Breakfast",
        "price": [
            "amount": 10.00,
            "currency": "GBP"
        ],
        "foodUpsell": true,
        "freeBreakfastTrigger": true,
        "freeBreakfastOption": true
    ]

    private let continentalBreakfast: PIDictionary = [
        "operaId": "BFADCT",
        "code": 12,
        "legend": "Continental Breakfast",
        "price": [
            "amount": 8.00,
            "currency": "GBP"
        ],
        "foodUpsell": true,
        "freeBreakfastTrigger": false,
        "freeBreakfastOption": false
    ]

    private let mealDeal: PIDictionary = [
        "operaId": "MDP",
        "code": 17,
        "legend": "Meal Deal (Breakfast & Dinner)",
        "price": [
            "amount": 25.00,
            "currency": "GBP"
        ],
        "foodUpsell": true,
        "freeBreakfastTrigger": true,
        "freeBreakfastOption": true
    ]

    private let germanBreakfast: PIDictionary = [
        "operaId": "BFADBF",
        "code": 11,
        "legend": "Breakfast",
        "price": [
            "amount": 15.00,
            "currency": "EUR"
        ],
        "foodUpsell": true,
        "freeBreakfastTrigger": true,
        "freeBreakfastOption": true
    ]

    func testMealDealOfferOrKidsEatFreeBreakfastCopy() {
        
        let bookingDetails = BookingDetails()

        bookingDetails.rate = Rate(dictionary: ["upsellItems": [piBreakfast, continentalBreakfast, mealDeal]])
        XCTAssertEqual(bookingDetails.mealInformationText, "Buy one Premier Inn Breakfast or Meal Deal and up to two under 16s eat breakfast FREE")

        bookingDetails.rate = Rate(dictionary: ["upsellItems": [piBreakfast]])
        XCTAssertEqual(bookingDetails.mealInformationText, "Buy one Premier Inn Breakfast and up to two under 16s eat breakfast FREE")

        bookingDetails.rate = Rate(dictionary: ["upsellItems": [mealDeal]])
        XCTAssertEqual(bookingDetails.mealInformationText, "Buy one Meal Deal (Breakfast & Dinner) and up to two under 16s eat breakfast FREE")

        bookingDetails.rate = Rate(dictionary: ["upsellItems": [continentalBreakfast, mealDeal]])
        XCTAssertEqual(bookingDetails.mealInformationText, "Buy one Meal Deal (Breakfast & Dinner) and up to two under 16s eat breakfast FREE")

        bookingDetails.rate = Rate(dictionary: ["upsellItems": [continentalBreakfast]])
        XCTAssertNil(bookingDetails.mealInformationText)

        bookingDetails.rate = Rate(dictionary: ["upsellItems": []])
        XCTAssertNil(bookingDetails.mealInformationText)

        bookingDetails.rate = Rate(dictionary: [:])
        XCTAssertNil(bookingDetails.mealInformationText)

        bookingDetails.rate = Rate(dictionary: ["upsellItems": [mealDeal, piBreakfast]])
        XCTAssertEqual(bookingDetails.mealInformationText, "Buy one Premier Inn Breakfast or Meal Deal and up to two under 16s eat breakfast FREE")

        bookingDetails.rate = Rate(dictionary: ["upsellItems": [germanBreakfast]])
        XCTAssertEqual(bookingDetails.mealInformationText, "Buy one Breakfast and up to two under 16s eat breakfast FREE")

        bookingDetails.rate = Rate(dictionary: ["upsellItems": [germanBreakfast, mealDeal]])    // future
        XCTAssertEqual(bookingDetails.mealInformationText, "Buy one Breakfast or Meal Deal and up to two under 16s eat breakfast FREE")

    }

    func getRoomTypeInformation() -> [RoomTypeInformation] {
        let fileURL = Bundle(for: type(of: self)).url(forResource: "RoomTypeInformationGraphQLResponse", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let object = try! JSONSerialization.jsonObject(with: data) as! PIDictionary
        let dataDict = object["data"] as! PIDictionary
        let roomTypesDict = dataDict["roomTypeInformation"] as! PIDictionary
        let roomTypes = roomTypesDict["roomTypes"] as! [PIDictionary]
        let roomTypeContentData = try! JSONSerialization.data(withJSONObject: roomTypes, options: .prettyPrinted)
        return try! JSONDecoder().decode([RoomTypeInformation].self, from: roomTypeContentData)
    }

    func testSubstitutions_InBookingWithOneRoom_WithTwoRoomOptions() {
        let bookingDetails = BookingDetails()
        SettingsManager.sharedInstance.roomTypesContent = getRoomTypeInformation()
        
        let roomsResponseDictionary: [String: Any] = [
            "rooms": [[
                "type": "DB",
                "adults": 1,
                "children": 0,
                "options": [
                    [
                        "lettingType": "PPLDBL",
                        "silentSubstitution": false,
                        "roomClass": "PP",
                        "totalCost": [
                            "amount": "73.00",
                            "currency": "GBP"
                        ]
                    ],
                    [
                        "lettingType": "DOUBLE",
                        "silentSubstitution": false,
                        "roomClass": "ST",
                        "totalCost": [
                            "amount": "53.00",
                            "currency": "GBP"
                        ]
                    ]
                ]
            ]]
        ]
        let rate = Rate(dictionary: roomsResponseDictionary)

        let criteriaWithRooms: [String: Any] = [
            "rooms": [[ "type": "FAM"]]
        ]
        var criteria = Criteria()
        criteria.rooms = (criteriaWithRooms["rooms"] as! [PIDictionary]).map { Room(dictionary: $0) }
        bookingDetails.criteria = criteria

        let substitutions = bookingDetails.substitutions(forRate: rate)
        XCTAssertEqual(substitutions.count, 1)
        XCTAssertEqual(substitutions.first!.desired, .family)
        XCTAssertEqual(substitutions.first!.substituted, .double)
        XCTAssertEqual(substitutions.first!.substitutedRoomsConcatenated, "Premier Plus room or Double room")
    }
}
