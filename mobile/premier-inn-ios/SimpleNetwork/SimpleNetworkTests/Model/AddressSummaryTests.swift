//
//  AddressSummaryTests.swift
//  SimpleNetworkTests
//
//  Created by Georgios Aikaterinakis on 27/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class AddressSummaryTests: XCTestCase {

    override func setUp() {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    private var goodDictionary: [String: String] = [
        "id": "1234",
        "addressText": "2 Festoon Way, London E16 1SJ"
    ]

    func testAddressSummary() {

        XCTAssertThrowsError(try AddressSummary(dictionary: nil, postCode: "E16 1SJ")) { error in
            XCTAssertEqual(error as? AddressSummaryError, AddressSummaryError.missingAddressSummaryDictionary)
        }
        XCTAssertThrowsError(try AddressSummary(dictionary: ["addressText": "something"], postCode: "E16 1SJ")) { error in
            XCTAssertEqual(error as? AddressSummaryError, AddressSummaryError.missingId)
        }
        XCTAssertNoThrow(try AddressSummary(dictionary: goodDictionary, postCode: "E16 1SJ"))

        let addressSummary = try? AddressSummary(dictionary: goodDictionary, postCode: "E16 1SJ")
        XCTAssertEqual(addressSummary?.id, "1234")
        XCTAssertEqual(addressSummary?.label, "2 Festoon Way, London E16 1SJ")
        XCTAssertEqual(addressSummary?.description, "2 Festoon Way, London E16 1SJ")
        XCTAssertEqual(addressSummary?.searchedPostCode, "E16 1SJ")
    }

}
