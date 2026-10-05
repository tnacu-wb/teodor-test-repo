//
//  ReservationPreference.swift
//  SimpleNetwork
//
//  Created by Muresan, Andreea (Cognizant) on 17.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

public struct PreferencesCollection {
    public let preferenceType: String
    public let preferences: [String]

    public init(preferenceType: String, preferences: [String]) {
        self.preferenceType = preferenceType
        self.preferences = preferences
    }
}

public struct ReservationPreference: Codable {
    public let code: String?
    public let preferenceType: String?
}
