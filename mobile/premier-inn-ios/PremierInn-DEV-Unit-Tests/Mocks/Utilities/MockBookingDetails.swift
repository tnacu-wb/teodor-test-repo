//
//  MockBookingDetails.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
@testable import SimpleNetwork

final class MockBookingDetails: BookingDetails {

    var employeeRatesEnabledValue: Bool?

    override var employeeRatesEnabled: Bool {
        get { employeeRatesEnabledValue ?? false }
        set { employeeRatesEnabledValue = newValue }
    }
}
