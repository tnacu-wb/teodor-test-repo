//
//  RoomClassConfigTests.swift
//  SimpleNetworkTests
//
//  Created by Santa Gurung on 03/01/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class RoomClassConfigTests: XCTestCase {

    func testRoomClassConfigDecoding() {
        let fileURL = Bundle.module.url(forResource: "RoomClassConfig", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let roomClassConfigDictionary = dataDictionary["roomClassConfig"] as! PIDictionary
        let roomClassConfigData = try! JSONSerialization.data(withJSONObject: roomClassConfigDictionary)

        do {
            let roomClassConfig = try JSONDecoder().decode(RoomClassConfig.self, from: roomClassConfigData)
            XCTAssertEqual(roomClassConfig.roomClassConfig[0].code, "ST")
            XCTAssertEqual(roomClassConfig.roomClassConfig[0].order, 1)
            XCTAssertEqual(roomClassConfig.roomClassConfig[1].code, "SF")
            XCTAssertEqual(roomClassConfig.roomClassConfig[1].order, 2)
        } catch {
            XCTFail("Decoding failed: \(error)")
        }
    }

}
