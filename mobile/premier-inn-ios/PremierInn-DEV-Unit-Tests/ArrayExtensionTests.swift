//
//  ArrayTests.swift
//  PremierInn
//
//  Created by Nick Jones on 22/01/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

class ArrayExtensionTests: XCTestCase {
    
    var emptyArray: [String] = []
    var nilArray: [String]? = nil
    var nonEmptyArray = [1, 3, 5]
    
    func testEmptyArrayIsEmpty() { XCTAssertTrue(emptyArray.isEmpty) }
    func testEmptyArrayIsNotNotEmpty() { XCTAssertFalse(emptyArray.isNotEmpty) }
    func testNilArrayIsEmptyReturnsNil() { XCTAssertNil(nilArray?.isEmpty) }
    func testNilArrayIsNotEmptyReturnsNil() { XCTAssertNil(nilArray?.isNotEmpty) }
    func testNonEmptyArrayIsEmpty() { XCTAssertFalse(nonEmptyArray.isEmpty) }
    func testNonEmptyArrayIsNotEmpty() { XCTAssertTrue(nonEmptyArray.isNotEmpty) }
}
