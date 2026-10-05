//
//  StringExtensions+matchesTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 10/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Testing
@testable import PremierInn

struct StringExtensionsMatchesTests {

    // MARK: - UK Postcode tests

    @Test(arguments: [
        ("SW1A 1AA", true),
        ("SW1A1AA", true),
        ("12345", false),
        ("SW1 123", false)
    ])
    func ukPostcodeMatches(string: String, expected: Bool) {
        // GIVEN
        let input = string

        // WHEN
        let result = input.matches(Constants.Regex.ukPostcode)

        // THEN
        #expect(result == expected)
    }

    // MARK: - German Postcode tests

    @Test(arguments: [
        ("10115", true),
        ("10A15", false),
        ("", false),
        ("101 15", false)
    ])
    func germanPostcodeMatches(string: String, expected: Bool) {
        // GIVEN
        let input = string

        // WHEN
        let result = input.matches(Constants.Regex.germanPostcode)

        // THEN
        #expect(result == expected)
    }

    // MARK: - Generic Regex tests

    @Test(arguments: [
        ("abc123", true),
        ("abc123!", false)
    ])
    func alphanumericRegexMatches(string: String, expected: Bool) {
        // GIVEN
        let input = string
        let regex = /[a-zA-Z0-9]+/

        // WHEN
        let result = input.matches(regex)

        // THEN
        #expect(result == expected)
    }

    @Test(arguments: [
        ("test@example.com", true),
        ("test@com", false)
    ])
    func emailRegexMatches(string: String, expected: Bool) {
        // GIVEN
        let input = string
        let regex = /\S+@\S+\.\S+/

        // WHEN
        let result = input.matches(regex)

        // THEN
        #expect(result == expected)
    }

    @Test(arguments: [
        ("abc123", false)
    ])
    func lettersOnlyRegex_withNumbers_returnsFalse(string: String, expected: Bool) {
        // GIVEN
        let input = string
        let regex = /[a-zA-Z]+/

        // WHEN
        let result = input.matches(regex)

        // THEN
        #expect(result == expected)
    }

    @Test(arguments: [
        ("hello", true),
        ("hello1", false)
    ])
    func exactMatchRegex(string: String, expected: Bool) {
        // GIVEN
        let input = string
        let regex = /hello/

        // WHEN
        let result = input.matches(regex)

        // THEN
        #expect(result == expected)
    }

    @Test(arguments: [
        ("   ", true)
    ])
    func whitespaceRegexMatches(string: String, expected: Bool) {
        // GIVEN
        let input = string
        let regex = /\s+/

        // WHEN
        let result = input.matches(regex)

        // THEN
        #expect(result == expected)
    }
}
