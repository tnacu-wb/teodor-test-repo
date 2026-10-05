//
//  PIDictionary+Extensions.swift
//  PremierInn
//
//  Created by Filippo Minelle on 07/07/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation

// MARK: - PIDictionary

public extension PIDictionary {
    /// A converted [String: String] dictionary from  [String: Any] dictionary using AdobeTrackable protocol
    var trackingDictionary: [String: String]? {
        let trackingStringDictionary: [String: String]? = self.compactMapValues { ($0 as? AdobeTrackable)?.trackingValue }

        return trackingStringDictionary
    }

    internal func mergeByKeepingAllValues(with dictionary: PIDictionary) -> PIDictionary {
        self.merging(dictionary) { current, _ in
            current
        }
    }
}

// MARK: - AdobeTrackable

protocol AdobeTrackable {
    var trackingValue: String { get }
}

extension Int: AdobeTrackable {
    var trackingValue: String {
        String(format: "%d", self)
    }
}

extension Double: AdobeTrackable {
    var trackingValue: String {
        String(format: "%f", self)
    }
}

extension Float: AdobeTrackable {
    var trackingValue: String {
        String(format: "%f", self)
    }
}

extension String: AdobeTrackable {
    var trackingValue: String {
        self
    }
}

extension Bool: AdobeTrackable {
    var trackingValue: String {
        "\(self)"
    }
}

extension Date: AdobeTrackable {
    var trackingValue: String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd/MM/yyyy"

        return dateFormatter.string(from: self)
    }
}

extension LoggedInAnalytic: AdobeTrackable {
    var trackingValue: String { self.rawValue }
}
