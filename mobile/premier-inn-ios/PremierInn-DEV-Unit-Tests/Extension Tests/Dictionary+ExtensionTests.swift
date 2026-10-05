//
//  Dictionary+ExtensionTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 06/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

final class DictionaryExtensionTests: XCTestCase {

    // MARK: - mergePreferNew tests

    func testMergeIntoEmptyDictionary() {
        // GIVEN
        var base: [String: Int] = [:]
        let incoming = ["a": 1, "b": 2]

        // WHEN
        base.mergePreferNew(incoming)

        // THEN
        XCTAssertEqual(base, incoming)
    }

    func testMergeWithNoOverlappingKeys() {
        // GIVEN
        var base = ["a": 1]
        let incoming = ["b": 2]

        // WHEN
        base.mergePreferNew(incoming)

        // THEN
        XCTAssertEqual(base, ["a": 1, "b": 2])
    }

    func testMergeWithOverlappingKeysPrefersNewValues() {
        // GIVEN
        var base = ["a": 1, "b": 2]
        let incoming = ["b": 20, "c": 3]

        // WHEN
        base.mergePreferNew(incoming)

        // THEN
        XCTAssertEqual(base, ["a": 1, "b": 20, "c": 3])
    }

    func testMergeWithEmptyDictionaryDoesNothing() {
        // GIVEN
        var base = ["a": 1, "b": 2]
        let incoming: [String: Int] = [:]

        // WHEN
        base.mergePreferNew(incoming)

        // THEN
        XCTAssertEqual(base, ["a": 1, "b": 2])
    }

    func testMergeEmptyWithEmpty() {
        // GIVEN
        var base: [String: Int] = [:]
        let incoming: [String: Int] = [:]

        // WHEN
        base.mergePreferNew(incoming)

        // THEN
        XCTAssertTrue(base.isEmpty)
    }
}
