//
//  FindBookingSource.swift
//  SimpleNetwork
//
//  Created by Santa Gurung on 28/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public enum BookingSourcePMS: String, Decodable {
    case bart = "Bart"
    case opera = "Opera"
}

public struct FindBookingSource: Decodable {
    public let sourcePms: BookingSourcePMS
    public let bookingReference: String
    public let token: String
    public let basketReference: String
    public let hotelId: String?
    public let isThirdPartyBooking: Bool?

    enum CodingKeys: String, CodingKey {
        case sourcePms
        case bookingReference = "ref"
        case token
        case basketReference
        case hotelId
        case isThirdPartyBooking
    }
}
