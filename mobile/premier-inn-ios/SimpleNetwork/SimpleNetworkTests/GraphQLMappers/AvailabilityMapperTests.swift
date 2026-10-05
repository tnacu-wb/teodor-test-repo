//
//  AvailabilityMapperTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 14/06/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import XCTest

@testable import SimpleNetwork

class AvailabilityMapperTests: XCTestCase {

    var sut = PIDictionary()

    override func setUp() {
        let fileURL = Bundle.module.url(forResource: "singleHotelAvailability_getPackages_Response", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let hotelInformationDictionary = dataDictionary["hotelAvailability"] as! PIDictionary
        let ratesInfo = (dataDictionary["ratesInformation"] as! PIDictionary)["rateClassifications"] as! [PIDictionary]
        let roomClassConfig = dataDictionary["roomClassConfig"] as! PIDictionary
        let roomClassArray = roomClassConfig["roomClassConfig"] as! [PIDictionary]
        sut = AvailabilityMapper.map(from: hotelInformationDictionary, and: ratesInfo, roomClassArray: roomClassArray)
    }

    func testInfoMapping() {
        XCTAssertEqual(sut["hotelCode"] as! String, "TLONEU")
        XCTAssertEqual(sut["limitedAvailability"] as! Bool, false)
        XCTAssertEqual(sut["available"] as! Bool, true)
    }

    func testRateMapping() {

        let rateType = sut["ratePlans"] as! [PIDictionary]

        for rate in rateType {
            XCTAssertEqual(rate["name"] as! String, "Flex")
        }
    }

    func testRoomMapping() {

        let rateType = sut["ratePlans"] as! [PIDictionary]

        for rate in rateType {
            let rooms = rate["rooms"] as! [PIDictionary]
            for room in rooms {
                XCTAssertEqual(room["type"] as! String, "DB")
            }
        }
    }

    func testPriceAndRoomClassMapping() {

        let rateType = sut["ratePlans"] as! [PIDictionary]
        for rate in rateType {

            let rooms = rate["rooms"] as! [PIDictionary]
            for room in rooms {

                let options = room["options"] as! [PIDictionary]
                for option in options {
                    
                    do {
                        // Total cost
                        let cost = try Cost(dictionary: option["totalCost"] as? PIDictionary)
                       
                        XCTAssertEqual(cost.amount, 180)
                        XCTAssertEqual(cost.currencyCode, "GBP")

                        // Room Class and Room Class Order
                        let roomClass = option["roomClass"] as! String
                        XCTAssertEqual(roomClass, "ST")
                        let roomClassOrder = option["roomClassOrder"] as! Int
                        XCTAssertEqual(roomClassOrder, 8)
                    }
                    catch {
                        XCTFail("Cost conversion Fail")
                    }

                }
            }
        }
    }
    
    func testRateInformationMapping() {
        let ratePlans = sut["ratePlans"] as! [PIDictionary]
        
        let flexRateDict = ratePlans.first { $0["name"] as! String == "Flex" }
        XCTAssertEqual(flexRateDict!["name"] as! String, "Flex")
        XCTAssertEqual(flexRateDict!["description"] as! String, "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival")
    }
    
    func testNumberOfRoomsAvailableMapping() {
        let ratePlans = sut["ratePlans"] as! [PIDictionary]
        
        for ratePlan in ratePlans {
            let rooms = ratePlan["rooms"] as! [PIDictionary]
            for room in rooms {
                let options = room["options"] as! [PIDictionary]
                for option in options {
                    XCTAssertEqual(option["numberAvailable"] as! Int, 5)
                }
            }
        }
    }
}
