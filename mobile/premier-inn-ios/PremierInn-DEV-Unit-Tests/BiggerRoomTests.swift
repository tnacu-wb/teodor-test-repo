//
//  BiggerRoomTests.swift
//  PremierInn
//
//  Created by Nick Jones on 17/07/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

class AlternativeRoomTests: XCTestCase {

    var hotelWithBiggerRooms: Hotel?
    var hotelWithOnlySomeBiggerRooms: Hotel?

    override func setUp() {
        super.setUp()

        let fileURLForBiggerRooms = Bundle(for: type(of: self)).url(forResource: "hotelInfoWithBiggerRooms", withExtension: "json")!
        let dataForBiggerRooms = try! Data(contentsOf: fileURLForBiggerRooms)
        let jsonDictionaryForBiggerRooms = try! JSONSerialization.jsonObject(with: dataForBiggerRooms, options: .allowFragments) as! PIDictionary

        hotelWithBiggerRooms = try? Hotel(dictionary: jsonDictionaryForBiggerRooms)

        let fileURLForSomeBiggerRooms = Bundle(for: type(of: self)).url(forResource: "hotelInfoWithBiggerRoomsForSomeRooms", withExtension: "json")!
        let dataForSomeBiggerRooms = try! Data(contentsOf: fileURLForSomeBiggerRooms)
        let jsonDictionaryForSomeBiggerRooms = try! JSONSerialization.jsonObject(with: dataForSomeBiggerRooms, options: .allowFragments) as! PIDictionary

        hotelWithOnlySomeBiggerRooms = try? Hotel(dictionary: jsonDictionaryForSomeBiggerRooms)
    }

    func testHotelsCanBeParsed() {

        XCTAssertNotNil(hotelWithBiggerRooms)
        XCTAssertNotNil(hotelWithOnlySomeBiggerRooms)
    }
}
