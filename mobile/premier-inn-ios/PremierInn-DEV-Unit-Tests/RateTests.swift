//
//  RateTests.swift
//  PremierInnTests
//
//  Created by Georgios Aikaterinakis on 09/09/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

class RateTests: XCTestCase {

    var ratesContent: [RateInformation]? {

        let ratesContentDictionary: [PIDictionary] = [
            [
                "rateClassification": "A",
                "rateOrder": "5",
                "rateName": "Flex",
                "rateDescription": "Zahlen Sie bei Ankunft. Änderbar oder stornierbar bis 18 Uhr am Anreisetag.",
                "rateNotes": "<p>test</p>\n",
                "brand": "PID"
            ],
            [
                "rateClassification": "F",
                "rateOrder": "5",
                "rateName": "Flex",
                "rateDescription": "Zahlen Sie bei Ankunft. Änderbar oder stornierbar bis 18 Uhr am Anreisetag.",
                "rateNotes": "<p>Kostenlose Stornierung und Änderung bis 18 Uhr am Anreisetag.</p>\n",
                "brand": "PID"
            ]
        ]

        guard let ratesContentData = try? JSONSerialization.data(withJSONObject: ratesContentDictionary, options: .prettyPrinted) else { return nil }

        do {
            let decoder = JSONDecoder()
            return try decoder.decode([RateInformation].self, from: ratesContentData)
        } catch {
            return nil
        }
    }

    func testRatesWithoutContent() {

        let fRate = Rate(dictionary: ["name": "Advance", "classification": "F"])
        let aRate = Rate(dictionary: ["name": "Flex", "classification": "A"])
        let customRate = Rate(dictionary: ["name": "Custom Flex", "classification": "F21"])
        var rates = [fRate, aRate, customRate]

        guard let ratesContent = self.ratesContent else {
            XCTFail("error occured while setting up ratesContent")
            return
        }

        // 1 rate without content
        var filteredRates = rates.ratesWithoutContent(ratesContent: ratesContent)
        XCTAssertEqual(filteredRates.count, 1)

        // no rateContent
        filteredRates = rates.ratesWithoutContent(ratesContent: [])
        XCTAssertEqual(filteredRates.count, 3)

        // all rates with content
        rates = [fRate, aRate]
        filteredRates = rates.ratesWithoutContent(ratesContent: ratesContent)
        XCTAssertEqual(filteredRates.count, 0)

    }

    func getRatesWithRoomClassOrder(filename: String) -> [Rate]? {
        let fileURL = Bundle(for: type(of: self)).url(forResource: filename, withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let hotelInformationDictionary = dataDictionary["hotelAvailability"] as! PIDictionary
        let roomClassConfig = dataDictionary["roomClassConfig"] as! PIDictionary
        let roomClassArray = roomClassConfig["roomClassConfig"] as! [PIDictionary]
        let ratePlans = RatePlansMapper.map(from: hotelInformationDictionary, and: nil, roomClassArray: roomClassArray)
        return ratePlans.map { Rate(dictionary: $0) }
    }
    
    func testRoomClassOrder_ByAPIOrder_WhenRoomClassConfigIsAvailable() {
        let rates = getRatesWithRoomClassOrder(filename: "hotelAvailabilityWithRoomClassConfig")
        let sorted = rates?.ratesSeparatedByTieredRooms
        asserts(for: sorted)
    }

    func testRoomClassOrder_ByHardcodedOrder_WhenRoomClassConfigIsEmpty() {
        let rates = getRatesWithRoomClassOrder(filename: "hotelAvailabilityWithEmptyRoomClassConfig")
        let sorted = rates?.ratesSeparatedByTieredRooms
        asserts(for: sorted)
    }

    func testRoomClassOrder_WhenRoomClassConfigHasSomeMissingRoomClass() {
        let rates = getRatesWithRoomClassOrder(filename: "hotelAvailabilityWithMissingRoomClassConfig")
        let sorted = rates?.ratesSeparatedByTieredRooms
        asserts(for: sorted)
    }

    func asserts(for sorted: [LettingOptionRates]?) {
        XCTAssertEqual(sorted?[0].roomClassOptions.first?.roomClass, "ST")
        XCTAssertEqual(sorted?[1].roomClassOptions.first?.roomClass, "SV")
        XCTAssertEqual(sorted?[2].roomClassOptions.first?.roomClass, "PP")
        XCTAssertEqual(sorted?[3].roomClassOptions.first?.roomClass, "PV")
    }
}
