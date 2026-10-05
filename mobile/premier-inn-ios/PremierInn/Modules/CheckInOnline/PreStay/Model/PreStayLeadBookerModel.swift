//
//  PreStayLeadBookerModel.swift
//  PremierInn
//
//  Created by Clint Mengolli on 25/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SimpleNetwork

/// Provides the Lead Booker details used during Pre-Stay.
/// ----------
/// Reservation data differs between **Direct bookings** and **3rd-party bookings**
/// (e.g. Booking.com). For many non-direct imports the `billing`/`booker` object
/// is `nil`, even though guest details exist
///
/// Business assumptions (confirmed during spike investigation [CTECH-6037](https://whitbreadis.atlassian.net/browse/CTECH-6037):
/// - The **Lead Guest in Room 1** (`isAccompanyingGuest == false`) represents the
///   person who made the booking
/// - Guests in other rooms are never considered lead guests
/// - If billing details are missing for a 3rd-party booking, the Lead Guest of
///   Room 1 is used as the **Lead Booker**
///
/// Additional notes:
/// - Email and phone are not expected for 3rd-party imports, so they
///   are returned as `nil` to prompt the user to enter them manually in CIOL
/// - Address data may be present and remains editable
///

struct PreStayLeadBookerModel {
    private enum UserSource {
        case booker
        case leadGuest
    }

    private let reservation: Reservation
    private let firstRoom: Room?
    private let source: UserSource

    init(reservation: Reservation) {
        self.reservation = reservation
        self.firstRoom = reservation.rooms.first

        if reservation.isDirect {
            self.source = .booker
        } else if reservation.leadGuest?.isMissingBillingDetails ?? true {
            self.source = .leadGuest
        } else {
            self.source = .booker
        }
    }

    private var leadGuest: User? {
        firstRoom?.guestList?.first(where: { !($0.isAccompanyingGuest ?? true) })
    }

    var title: String? {
        switch source {
        case .booker:
            reservation.booker?.title
        case .leadGuest:
            leadGuest?.title
        }
    }

    var firstName: String? {
        switch source {
        case .booker:
            reservation.booker?.firstName
        case .leadGuest:
            leadGuest?.firstName
        }
    }

    var lastName: String? {
        switch source {
        case .booker:
            reservation.booker?.lastName
        case .leadGuest:
            leadGuest?.lastName
        }
    }

    var address: Address? {
        switch source {
        case .booker:
            reservation.booker?.address
        case .leadGuest:
            leadGuest?.address
        }
    }

    var email: String? {
        switch source {
        case .booker:
            reservation.booker?.emailAddress
        case .leadGuest:
            nil
        }
    }

    var phoneNumber: String? {
        switch source {
        case .booker:
            reservation.booker?.contactNumber
        case .leadGuest:
            nil
        }
    }
}
