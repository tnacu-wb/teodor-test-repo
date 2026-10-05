//
//  RestrictionsMapperTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 05/05/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import XCTest

class RestrictionsMapperTests: XCTestCase {
    var restrictions: [Restrictions]? = nil

override func setUp() {
    let fileURL = Bundle.module.url(forResource: "graphQLRestrictions", withExtension: "json")!
    let data = try! Data(contentsOf: fileURL)
    let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
    let dataDictionary = jsonDictionary["data"] as! PIDictionary

    let restrictionDictionary = RestrictionsMapper.map(input: dataDictionary)
    do {
        let restrictionData = try JSONSerialization.data(withJSONObject: restrictionDictionary, options: .prettyPrinted)

        let decoder = JSONDecoder()
        restrictions = try decoder.decode([Restrictions].self, from: restrictionData)
    } catch {
        XCTFail("error thrown when decoding HeaderInformation")
    }

}

    func testRestrictions() {

        let leisureRestrictions = restrictions?.first(where: { $0.channel == .PI })
        XCTAssertEqual(leisureRestrictions!.maxNights, 9)
        XCTAssertEqual(leisureRestrictions!.maxDepartureDateCount, 365)
        XCTAssertEqual(leisureRestrictions!.maxRooms, 9)
    }
}
