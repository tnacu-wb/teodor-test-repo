//
//  BookingConfirmation.swift
//  PremierInn
//
//  Created by Marcello Mascia on 19/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation

public struct BookingConfirmation: Decodable {
    public let confirmationNumber: String?
    public let bookingConfirmationOperaStatus: Basket?

    enum CodingKeys: String, CodingKey {
        case confirmationNumber
        case bookingConfirmationOperaStatus = "basketStatus"
    }
}

public extension BookingConfirmation {
    static var mock: BookingConfirmation? {
        guard let dict = MockServices.getJsonDictionary(fileName: "booking") else { return nil }
        guard let dictData = try? JSONSerialization.data(withJSONObject: dict, options: .prettyPrinted) else { return nil }
        guard let bookingConfirmationMock = try? JSONDecoder().decode(BookingConfirmation.self, from: dictData)
            else { return nil }

        return bookingConfirmationMock
    }

    static var mockPollingComplete: BookingConfirmation? {
        guard let dict = MockServices.getJsonDictionary(fileName: "bookingConfirmationOperaPollingComplete")
            else { return nil }
        guard let dictData = try? JSONSerialization.data(withJSONObject: dict, options: .prettyPrinted) else { return nil }
        guard let bookingConfirmationMock = try? JSONDecoder().decode(BookingConfirmation.self, from: dictData)
            else { return nil }

        return bookingConfirmationMock
    }

    static var mockPollingFailed: BookingConfirmation? {
        guard let dict = MockServices.getJsonDictionary(fileName: "bookingConfirmationOperaPollingFailed")
            else { return nil }
        guard let dictData = try? JSONSerialization.data(withJSONObject: dict, options: .prettyPrinted) else { return nil }
        guard let bookingConfirmationMock = try? JSONDecoder().decode(BookingConfirmation.self, from: dictData)
            else { return nil }

        return bookingConfirmationMock
    }

    static var mockPollingCiolFailed: BookingConfirmation? {
        guard let dict = MockServices.getJsonDictionary(fileName: "bookingConfirmationOperaPollingCiolFailed")
            else { return nil }
        guard let dictData = try? JSONSerialization.data(withJSONObject: dict, options: .prettyPrinted) else { return nil }
        guard let bookingConfirmationMock = try? JSONDecoder().decode(BookingConfirmation.self, from: dictData)
            else { return nil }

        return bookingConfirmationMock
    }
}
