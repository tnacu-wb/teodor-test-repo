//
//  AvailabilitiesMapperTests.swift
//  SimpleNetworkTests
//
//  Created by Santa Gurung on 08/02/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class AvailabilitiesMapperTests: XCTestCase {

    var sut = PIDictionary()

    override func setUp() {
        super.setUp()
        let fileURL = Bundle.module.url(forResource: "graphQLAvailabilities", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let hotelAvailabilities = dataDictionary["hotelAvailabilities"] as! PIDictionary
        let multiHotelAvailabilities = hotelAvailabilities["multiHotelAvailabilities"] as! [PIDictionary]
        sut = AvailabilitiesMapper.map(from: multiHotelAvailabilities).last!
    }

    func testHotelAvailabilityMapping() {
        XCTAssertEqual(sut["hotelCode"] as! String, "LONEUS")
        XCTAssertEqual(sut["hotelBrand"] as! String, "PI")
        XCTAssertEqual(sut["available"] as! Bool, true)
        XCTAssertEqual(sut["limitedAvailability"] as! Bool, true)
        XCTAssertEqual(sut["distance"] as! Double, 1.84)
    }

    func testHotelInfoMapping() {
        let hotelInfo = sut["hotelInfo"] as! PIDictionary
        XCTAssertEqual(hotelInfo["code"] as! String, "LONEUS")
        XCTAssertEqual(hotelInfo["brand"] as! String, "PI")
        XCTAssertEqual(hotelInfo["name"] as! String, "London Euston")
    }

    func testFacilitiesMapping() {
        let hotelInfo = sut["hotelInfo"] as! PIDictionary
        let facilities = hotelInfo["facilities"] as! [PIDictionary]
        let facility = facilities.first!
        XCTAssertEqual(facility["code"] as! String, "COP")
        XCTAssertEqual(facility["legend"] as! String, "Chargeable offsite parking")
    }

    func testMessagingFlagMapping() {
        let hotelInfo = sut["hotelInfo"] as! PIDictionary
        let messagingFlag = hotelInfo["messagingFlag"] as! PIDictionary
        XCTAssertEqual(messagingFlag["flagText"] as! String, "pi")
        XCTAssertEqual(messagingFlag["flagColor"] as! String, "BCD01B")
    }

    func testCoordinatesMapping() {
        let hotelInfo = sut["hotelInfo"] as! PIDictionary
        let map = hotelInfo["map"] as! PIDictionary
        XCTAssertEqual(map["latitude"] as! Double, 51.527736)
        XCTAssertEqual(map["longitude"] as! Double, -0.129068)
    }

    func testThumbnailImageMapping() {
        let hotelInfo = sut["hotelInfo"] as! PIDictionary
        let images = hotelInfo["images"] as! [PIDictionary]
        let image = images.first!
        XCTAssertEqual(image["fileReference"] as! String, "/content/dam/pi/websites/hotelimages/gb/en/L/LONEUS/LONEUS 1.jpg")
        XCTAssertEqual(image["tags"] as! [String], ["surrounding-area"])
    }
}

