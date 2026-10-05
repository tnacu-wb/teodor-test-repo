//
//  BiggerRoomTests.swift
//  SimpleNetworkTests
//
//  Created by Nick Jones on 17/07/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

class BiggerRoomTests: XCTestCase {

    var hotel: Hotel?

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
        let fileURL = Bundle.module.url(forResource: "hotelInfoWithBiggerRooms", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        hotel = try? Hotel(dictionary:jsonDictionary)
    }

    func testHotelCanBeParsed() {

        XCTAssertNotNil(hotel)
    }
}
