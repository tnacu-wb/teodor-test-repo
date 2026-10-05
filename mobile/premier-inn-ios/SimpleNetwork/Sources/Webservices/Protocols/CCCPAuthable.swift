//
//  CCCPAuthable.swift
//  SimpleNetwork
//
//  Created by Freddie Parks on 23/04/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import Foundation

public protocol CCCPStayDetails {
    var hotelIdentifier: String? { get }
    var hotelName: String? { get }
    var arrivalDate: Date? { get }
    var departureDate: Date? { get }
    var leadGuest: User? { get }
    var roomDetails: [PIDictionary]? { get }
    var businessAccount: BusinessAccount? { get }
}

extension BookingDetails: CCCPStayDetails {
    public var hotelIdentifier: String? {
        hotel?.code
    }

    public var hotelName: String? {
        hotel?.name
    }

    public var arrivalDate: Date? {
        criteria.arrivalDate
    }

    public var departureDate: Date? {
        criteria.checkOutDate
    }

    public var leadGuest: User? {
        booker
    }

    public var roomDetails: [PIDictionary]? {
        guard let rate = rate else { return nil }

        return roomLettings?.compactMap { roomLetting in
            let selectedOption = roomLetting.options?
                .first(where: { $0.lettingType == roomLetting.lettingType }) ?? roomLetting.options?.first

            return [
                "rate": rate.classification ?? "",
                "type": selectedOption?.lettingType ?? "",
                "adults": roomLetting.adults
            ]
        }
    }
}

extension Reservation: CCCPStayDetails {
    public var hotelIdentifier: String? {
        hotelCode
    }

    public var hotelName: String? {
        nil  // not available in Reservation
    }

    public var departureDate: Date? {
        checkOutDate
    }

    public var leadGuest: User? {
        booker
    }

    public var roomDetails: [PIDictionary]? {
        nil
    }

    public var businessAccount: BusinessAccount? {
        nil // not available in Reservation
    }
}
