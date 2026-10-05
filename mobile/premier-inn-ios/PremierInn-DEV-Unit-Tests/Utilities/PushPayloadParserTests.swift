//
//  PushPayloadParserTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

final class PushPayloadParserTests: XCTestCase {

    // MARK: - Tests

    func testExtractFiltersOutSystemKeys() {
        // GIVEN: payload containing system and custom keys
        let payload: [AnyHashable: Any] = [
            "google.c.a.e": "1",
            "gcm.messageId": "abc",
            "aps": "someValue",
            "customKey": "customValue"
        ]

        // WHEN: extracting payload
        let result = PushPayloadParser.extract(from: payload)

        // THEN: system keys are removed and custom key remains
        XCTAssertNil(result["google.c.a.e"])
        XCTAssertNil(result["gcm.messageId"])
        XCTAssertNil(result["aps"])
        XCTAssertEqual(result["customKey"] as! String, "customValue")
    }

    func testExtractKeepsOnlyStringKeyValuePairs() {
        // GIVEN: payload with mixed types
        let payload: [AnyHashable: Any] = [
            "validKey": "validValue",
            "intValue": 123,
            "boolValue": true,
            456: "invalidKeyType"
        ]

        // WHEN
        let result = PushPayloadParser.extract(from: payload)

        // THEN: only string-to-string pairs are preserved
        XCTAssertEqual(result["validKey"] as! String, "validValue")
        XCTAssertNil(result["intValue"])
        XCTAssertNil(result["boolValue"])
    }

    func testExtractReturnsEmptyDictionaryForEmptyPayload() {
        // GIVEN: empty payload
        let payload: [AnyHashable: Any] = [:]

        // WHEN
        let result = PushPayloadParser.extract(from: payload)

        // THEN
        XCTAssertTrue(result.isEmpty)
    }

    func testExtractReturnsEmptyDictionaryWhenOnlySystemKeysPresent() {
        // GIVEN: payload containing only filtered keys
        let payload: [AnyHashable: Any] = [
            "google.test": "value",
            "gcm.id": "123",
            "aps": "data"
        ]

        // WHEN
        let result = PushPayloadParser.extract(from: payload)

        // THEN: result should be empty
        XCTAssertTrue(result.isEmpty)
    }

    func testExtractPreservesMultipleValidEntries() {
        // GIVEN
        let payload: [AnyHashable: Any] = [
            "key1": "value1",
            "key2": "value2"
        ]

        // WHEN
        let result = PushPayloadParser.extract(from: payload)

        // THEN
        XCTAssertEqual(result["key1"] as! String, "value1")
        XCTAssertEqual(result["key2"] as! String, "value2")
        XCTAssertEqual(result.count, 2)
    }
}
