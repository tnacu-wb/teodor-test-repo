//
//  BookingFlowManager.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 29/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

class DispatchGroupManager {
    public static let sharedInstance = DispatchGroupManager()

    let appShortcutsDispatchGroup = DispatchGroup()
    let availabilityDispatchGroup = DispatchGroup()
    let holdBookingDispatchGroup = DispatchGroup()
    let bookingFlowDispatchGroup = DispatchGroup()
    let countriesDispatchGroup = DispatchGroup()
}
