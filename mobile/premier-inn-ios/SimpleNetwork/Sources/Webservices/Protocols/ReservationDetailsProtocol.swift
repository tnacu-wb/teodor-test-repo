//
//  StayReservation.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 06/12/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public protocol ReservationDetailsProtocol {
    var reservationIdentifier: String? { get }
    var surname: String? { get }
    var arrivalDate: Date? { get }
    var business: Bool { get }
    var token: String? { get }
}

extension Stay: ReservationDetailsProtocol {
    public var reservationIdentifier: String? {
        operaBasketReference
    }

    public var surname: String? {
        lastName
    }

    public var business: Bool {
        isBusinessTrip
    }
}

extension Reservation: ReservationDetailsProtocol {
    public var reservationIdentifier: String? {
        confirmationNumber
    }

    public var surname: String? {
        booker?.lastName
    }

    public var business: Bool {
        businessTrip
    }
}
