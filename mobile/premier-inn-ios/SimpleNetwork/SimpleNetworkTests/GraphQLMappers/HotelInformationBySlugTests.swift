//
//  HotelInformationBySlugTests.swift
//  SimpleNetworkTests
//
//  Created by Florin Velesca on 29.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

final class HotelInformationBySlugTests: XCTestCase {

    var sut = PIDictionary()

    override func setUp() {
        super.setUp()
        let fileURL = Bundle.module.url(forResource: "graphQLHotelBySlug", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! [String: Any]
        let dataDictionary = jsonDictionary["data"] as! [String: Any]
        let hotelInfoDict = dataDictionary["hotelInformationBySlug"] as! [String: Any]
        sut = HotelInformationMapper.map(from: hotelInfoDict, disclaimer: nil)
    }

    func testHotelMapping() {
        XCTAssertEqual(sut["brand"] as! String, "PI")
        XCTAssertEqual(sut["name"] as! String, "Brighton City Centre")
        XCTAssertEqual(sut["hotelCode"] as! String, "BRIPTI")
    }

}
