//
//  ReservationUpsellsMapperTests.swift
//  SimpleNetworkTests
//
//  Created by Santa Gurung on 29/07/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class ReservationUpsellsMapperTests: XCTestCase {

    var sut: [PIDictionary] = []

    override func setUp() {
        let fileURL = Bundle.module.url(forResource: "graphQLBookingConfirmationWithPackages", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let packagesDictionary = dataDictionary["packages"] as! PIDictionary

        sut = ReservationUpsellsMapper.map(from: packagesDictionary)!
    }

    func testUsersBookedPackagesCount() {
        XCTAssertEqual(sut.count, 2)
    }

    func testExtraItemsMapping() {
        let extraItems = sut.filter { $0["isExtraUpsell"] != nil && $0["isExtraUpsell"] as! Bool == true }
        XCTAssertEqual(extraItems.count, 1)
    }

    func testEarlyCheckinMapping() {
        let earlyCheckinItem = sut.first(where: { $0["operaId"] as! String == "HSCKIN" })!
        XCTAssertEqual(earlyCheckinItem["legend"] as! String, "Early check-in")
        XCTAssertEqual(earlyCheckinItem["description"] as! String, "Check out any time until 2pm (normal check-out time is 12pm).")
        XCTAssertEqual(earlyCheckinItem["quantity"] as! Int, 1)
        XCTAssertEqual(earlyCheckinItem["roomId"] as! String, "3767598")
    }
}
