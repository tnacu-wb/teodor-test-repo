//
//  FrequentlyBookedComponent.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 17/03/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import Foundation

public struct FrequentlyBooked: Codable {
    public let frequentBookings: [FrequentlyBookedHotel]?
}

public struct FrequentlyBookedHotel: Codable {
    public let hotelImage: String?
    public let hotelName: String?
    public let hotelCode: String
    public let hotelBrand: HotelBrand
}
