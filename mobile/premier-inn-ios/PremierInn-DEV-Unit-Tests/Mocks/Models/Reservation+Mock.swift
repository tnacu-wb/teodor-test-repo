//
//  Reservation+Mock.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 08/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

extension Reservation {
    static var mock: Self {
        try! .init(
            dictionary: [
            "reservationDetails": [
                "hotelCode": "LINMIL",
                "hotelName": "Hotel de ville",
                "identifier": "1234",
                "checkOutDate": "2017-10-12",
                "confirmationNumber": "BBER264250",
                "arrivalDate": "2017-10-11",
                "departureDate": "2017-10-12",
                "booker": [:],
                "rooms": [["bookingStatus": "InHouse"]],
                "roomBreakdown": [["roomId": "12345"]],
                "operaBasketReference": "123456789"
            ]]
        )
    }
}
