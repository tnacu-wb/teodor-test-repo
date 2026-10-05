//
//  StringExtensionsTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Nick Jones on 22/01/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

class StringExtensionTests: XCTestCase {
    
    var emptyString: String = ""
    var nilString: String? = nil
    var nonEmptyString: String = "pepega"
    
    func testEmptyStringIsEmpty() { XCTAssertTrue(emptyString.isEmpty) }
    func testEmptyStringIsNotNotEmpty() { XCTAssertFalse(emptyString.isNotEmpty) }
    func testNilStringIsEmptyReturnsNil() { XCTAssertNil(nilString?.isEmpty) }
    func testNilStringIsNotEmptyReturnsNil() { XCTAssertNil(nilString?.isNotEmpty) }
    func testNonEmptyStringIsEmpty() { XCTAssertFalse(nonEmptyString.isEmpty) }
    func testNonEmptyStringIsNotEmpty() { XCTAssertTrue(nonEmptyString.isNotEmpty) }
}
