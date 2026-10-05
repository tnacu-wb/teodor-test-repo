//
//  Criteria.swift
//  PremierInn
//
//  Created by Marcello Mascia on 07/04/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation

public struct Criteria {
	private var _arrivalDate = Date()
    public var arrivalDate: Date {
        get {
            if _arrivalDate.isInThePast {
                return Date()
            }

            return _arrivalDate
        }
        set {
            _arrivalDate = newValue
        }
    }
    public var nights = 1
	public var rooms = [Room()]

    public var checkOutDate: Date? { arrivalDate.dateByAddingUnit(unitType: .day, number: nights) }
    public var adultsCount: Int { rooms.map { $0.adults }.reduce(0, +) }
    public var childrenCount: Int { rooms.map { $0.children }.reduce(0, +) }
    public var guestsCount: Int { rooms.map { $0.children + $0.adults + $0.infants }.reduce(0, +) }

	public init() {}

    init(arrivalDate: Date?) {
        self.arrivalDate = arrivalDate ?? Date()
    }
}

private extension Date {
    var isInThePast: Bool {
        let currentCalendar = Calendar.current
        let dateComponents = currentCalendar.dateComponents([.day, .month, .year], from: Date())

        guard let today = currentCalendar.date(from: dateComponents) else { return false }

        return isBefore(date: today)
    }

    func isBefore(date: Date) -> Bool {
        self.compare(date) == .orderedAscending
    }
}
