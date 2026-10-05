//
//  Announcement+Extensions.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 20/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

extension Announcement: BookingDatesInfoProtocol {
    public func announcementToShow(arrivalDate: Date, departureDate: Date?) -> String? {
        guard showAnnouncement else { return nil }
        guard datesOverlap(arrivalDate: arrivalDate, departureDate: departureDate) else { return nil }
        guard let text = text?.htmlStripped(), text.isEmpty == false else { return nil }

        return text
    }
}

extension Note: BookingDatesInfoProtocol {}
