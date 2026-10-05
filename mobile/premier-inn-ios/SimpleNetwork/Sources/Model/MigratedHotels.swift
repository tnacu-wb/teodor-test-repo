//
//  MigratedHotels.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 10/10/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public struct MigratedHotel {
    public let code: String
    public let brand: HotelBrand

    public init?(dictionary: PIDictionary) {
        guard let code = dictionary["code"] as? String else { return nil }

        guard let hotelBrand = Hotel.hotelBrandString(dictionary: dictionary) else { return nil }
        guard let brand = HotelBrand(rawValue: hotelBrand) else { return nil}

        self.code = code
        self.brand = brand
    }
}
