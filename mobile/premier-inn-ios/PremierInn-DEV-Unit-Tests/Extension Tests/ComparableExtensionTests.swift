//
//  ComparableExtensionTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 26/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Testing
@testable import PremierInn

struct ComparableExtensionTests {

    @Test
    func valueWithinRangeReturnsSameValue() {
        // GIVEN a value inside the range
        let value = 5

        // WHEN clamping to 0...10
        let result = value.clamped(to: 0...10)

        // THEN it returns the original value
        #expect(result == 5)
    }

    @Test
    func valueBelowRangeReturnsLowerBound() {
        // GIVEN a value below the range
        let value = -3

        // WHEN clamping to 0...10
        let result = value.clamped(to: 0...10)

        // THEN it returns the lower bound
        #expect(result == 0)
    }

    @Test
    func valueAboveRangeReturnsUpperBound() {
        // GIVEN a value above the range
        let value = 42

        // WHEN clamping to 0...10
        let result = value.clamped(to: 0...10)

        // THEN it returns the upper bound
        #expect(result == 10)
    }

    @Test
    func doubleValueClampsCorrectly() {
        // GIVEN a Double value above the range
        let value: Double = 3.14

        // WHEN clamping to 0.0...3.0
        let result = value.clamped(to: 0.0...3.0)

        // THEN it returns the upper bound
        #expect(result == 3.0)
    }

    @Test
    func valuesOnBoundsRemainUnchanged() {
        // GIVEN values exactly on the bounds
        let lower = 0
        let upper = 10

        // WHEN clamping to 0...10
        let lowerResult = lower.clamped(to: 0...10)
        let upperResult = upper.clamped(to: 0...10)

        // THEN both values remain unchanged
        #expect(lowerResult == 0)
        #expect(upperResult == 10)
    }
}
