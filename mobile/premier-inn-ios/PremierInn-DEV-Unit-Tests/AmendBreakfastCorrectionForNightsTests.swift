//
//  AmendBreakfastCorrectionForNightsTEsts.swift
//  PremierInnDEVUnitTests
//
//  Created by Nick Jones on 11/04/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import SimpleNetwork
import XCTest
@testable import PremierInn

class AmendBreakfastCorrectionForNightsTests: XCTestCase {

    var breakfasts: [UpsellItem]?

    override func setUp() {

        let fileURLForBreakfasts = Bundle(for: type(of: self)).url(forResource: "breakfasts", withExtension: "json")!
        let dataForBreakfasts = try! Data(contentsOf: fileURLForBreakfasts)
        let jsonDictionaryForBreakfasts = try! JSONSerialization.jsonObject(with: dataForBreakfasts, options: .allowFragments) as! PIDictionary

        guard let breakfastsDictionaries = jsonDictionaryForBreakfasts["breakfasts"] as? [PIDictionary] else { return }

        breakfasts = breakfastsDictionaries.compactMap { try? UpsellItem(dictionary: $0) }
    }

    func testBreakfastsForBookingCorrectlySquashesTheNumberOfBreakfastsByRoom() {

        guard let breakfasts = breakfasts else { return XCTFail("Breakfasts data unavailable") }
        guard breakfasts.count == 9 else { return XCTFail("Expected 9 breakfasts in total but found \(breakfasts.count)") }

        guard breakfasts.reduce(0, { $0 + (($1.roomId == "DBS,1") ? 1 : 0) } ) == 3 else { return XCTFail("Expected 3 breakfasts for room DBS,1") }
        guard breakfasts.reduce(0, { $0 + (($1.roomId == "DBS,2") ? 1 : 0) } ) == 3 else { return XCTFail("Expected 3 breakfasts for room DBS,2") }
        guard breakfasts.reduce(0, { $0 + (($1.roomId == "DBS,3") ? 1 : 0) } ) == 3 else { return XCTFail("Expected 3 breakfasts for room DBS,3") }

        let breakfastsForBooking = breakfasts.squashedById()

        guard breakfastsForBooking.count == 3 else { return XCTFail("Expected 3 breakfasts in total but found \(breakfastsForBooking.count)") }

        guard breakfastsForBooking.reduce(0, { $0 + (($1.roomId == "DBS,1") ? 1 : 0) } ) == 1 else { return XCTFail("Expected 1 breakfast for room DBS,1") }
        guard breakfastsForBooking.reduce(0, { $0 + (($1.roomId == "DBS,2") ? 1 : 0) } ) == 1 else { return XCTFail("Expected 1 breakfast for room DBS,2") }
        guard breakfastsForBooking.reduce(0, { $0 + (($1.roomId == "DBS,3") ? 1 : 0) } ) == 1 else { return XCTFail("Expected 1 breakfast for room DBS,3") }
    }


    func testFoo() {

        let strings = ["Nick", "Freddie", "Keiron", "Keiron", "Keiron", "Freddie", "Nick"]

        let uniqueStrings = Array(Set(strings))

        XCTAssert(uniqueStrings.count == 3)
    }
}
