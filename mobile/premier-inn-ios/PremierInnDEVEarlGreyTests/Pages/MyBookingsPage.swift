//
//  MyBookingsPage.swift
//  PremierInnUITests
//
//  Created by Georgios Aikaterinakis on 02/09/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import EarlGrey

struct MyBookingsPage {

    static let searchTabBarItem = EarlGrey.selectElement(with: grey_accessibilityID("searchTabBarItem"))
    static let accountTabBarItem = EarlGrey.selectElement(with: grey_accessibilityID("accountTabBarItem"))

    private let bookingDetailsButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Booking details")).atIndex(0)

    static func goToSearch() { searchTabBarItem.perform(grey_tap()) }
    static func goToMyAccount() { accountTabBarItem.perform(grey_tap()) }

    func selectBookingDetails() {

        bookingDetailsButton.perform(grey_tap())
    }
}
