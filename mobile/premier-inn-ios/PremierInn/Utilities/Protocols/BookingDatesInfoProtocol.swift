//
//  BookingDatesInfoProtocol.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 12/01/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

protocol BookingDatesInfoProtocol {
    var startDate: Date? { get }
    var endDate: Date? { get }
    func datesOverlap(arrivalDate: Date, departureDate: Date?) -> Bool
}

extension BookingDatesInfoProtocol {
    func datesOverlap(arrivalDate: Date, departureDate: Date?) -> Bool {
        // when startDate, endDate are nil, it means there is no date restriction
        guard let startDate = startDate, let endDate = endDate else { return true }

        // startDate, endDate are not nil - but departureDate is nil, so do not show the announcement
        guard let departureDate = departureDate else { return false }

        // the inverse of this makes a lot more sense but doesn't go well with guard 🤓
        guard arrivalDate <= endDate && departureDate >= startDate else { return false }

        return true
    }
}
