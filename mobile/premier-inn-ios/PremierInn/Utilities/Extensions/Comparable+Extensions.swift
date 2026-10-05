//
//  Comparable+Extensions.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 26/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

extension Comparable {
    /// Returns this value clamped to the given closed range.
    ///
    /// If the value is less than the range’s lower bound, the lower bound is returned.
    /// If the value is greater than the range’s upper bound, the upper bound is returned.
    /// Otherwise, the value itself is returned unchanged.
    ///
    /// - Parameter limits: A closed range defining the lower and upper bounds.
    /// - Returns: A value guaranteed to be within `limits`.
    ///
    /// ## Examples
    ///
    /// ```swift
    /// let value = 5
    /// value.clamped(to: 0...10) // 5 (already within range)
    /// ```
    ///
    /// ```swift
    /// let value = -3
    /// value.clamped(to: 0...10) // 0 (clamped to lower bound)
    /// ```
    ///
    /// ```swift
    /// let value = 42
    /// value.clamped(to: 0...10) // 10 (clamped to upper bound)
    /// ```
    ///
    /// ```swift
    /// let value: Double = 3.14
    /// value.clamped(to: 0.0...3.0) // 3.0
    /// ```
    ///
    /// - SeeAlso:[StackOverflow](https://stackoverflow.com/questions/36110620/standard-way-to-clamp-a-number-between-two-values-in-swift)
    func clamped(to limits: ClosedRange<Self>) -> Self {
        min(max(self, limits.lowerBound), limits.upperBound)
    }
}
