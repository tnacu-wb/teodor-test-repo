//
//  ManageBookingPage.swift
//  PremierInnUITests
//
//  Created by Freddie Parks on 23/09/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import XCTest
import EarlGrey

class AmendBookingPage {

    // MARK: - Elements

    private let backButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("amendBackButton"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    private let amendDatesButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Edit dates"))
    private let amendMealsAndExtrasButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Change meals and extras"))
    private let addRoomButton = EarlGrey.selectElement(with: grey_accessibilityLabel("+ Add a room"))

    // MARK: - Actions

    func goBack() {
        backButton.perform(grey_tap())
    }

    func tapAmendDates() {
        amendDatesButton.perform(grey_tap())
    }

    func tapAddRoom() {

        GREYCondition(name: "scroll until add room button is visible") {

            var error: NSError?

            self.addRoomButton.usingSearch(grey_scrollInDirection(.down, 300), onElementWith: grey_accessibilityID("amendTableView")).perform(grey_tap(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.2)
    }

    // MARK: Validations

    func checkCanAmendMealsAndExtras() {
        amendMealsAndExtrasButton.assert(grey_sufficientlyVisible())
    }

    func checkCannotAmendMealsAndExtras() {
        amendMealsAndExtrasButton.assert(grey_notVisible())
    }
}
