//
//  HotelPreference.swift
//  SimpleNetwork
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 20.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

public struct HotelPreference: Codable {
    public var description: String?
    public var code: String?
    var preferenceGroup: String?
    var housekeeping: Bool?
    var orderSequence: Int?
    var hotelId: String?
    public var label: String?
}
