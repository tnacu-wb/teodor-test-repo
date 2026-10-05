//
//  ReservationDetails.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 06/12/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public struct ReservationDetails {
    public var reservationId: String
    public var surname: String
    public var arrivalDate: Date
    public var business: Bool
    public var token: String?

    public init?(stay: ReservationDetailsProtocol) {
        guard let reservationId = stay.reservationIdentifier else { return nil }
        guard let surname = stay.surname else { return nil }
        guard let arrivalDate = stay.arrivalDate else { return nil }

        self.reservationId = reservationId
        self.surname = surname
        self.arrivalDate = arrivalDate
        self.business = stay.business
        self.token = stay.token
    }
    public init(reservationId: String, surname: String, arrivalDate: Date, business: Bool, token: String?) {
        self.reservationId = reservationId
        self.surname = surname
        self.arrivalDate = arrivalDate
        self.business = business
        self.token = token
    }
}
enum ReservationDetailsError: LocalizedError {
    case missingReservationIdentifier
    case missingLastName
    case missingArrivalDate
}
