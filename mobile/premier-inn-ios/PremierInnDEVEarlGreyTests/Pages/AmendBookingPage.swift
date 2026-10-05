//
//  AmendBookingPage.swift
//  PremierInnUITests
//
//  Created by Freddie Parks on 23/09/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import XCTest
import EarlGrey

struct AmendBookingPage {

    // MARK: - Elements

    static let backButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("amendBackButton"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    static let amendDatesButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Change dates"))
    static let amendMealsAndExtrasButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Change meals and Wi-Fi"))
    static let addRoomButton = EarlGrey.selectElement(with: grey_accessibilityLabel("+ Add a room"))
    static let cancelBookingButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Cancel booking"))
    static let priceDifferenceLabel = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("priceDifferenceAcc")]))
    static let reviewAmendsButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("reviewAmendsAcc")]))
    static let editGuestsForRoom1Button = EarlGrey.selectElement(with: grey_accessibilityLabel("Change guests for Room 1"))

    // MARK: - Actions

    static func goBack() {
        backButton.perform(grey_tap())
    }

    static func tapAmendDates() {
        amendDatesButton.perform(grey_tap())
    }

    static func tapAmendMealsAndExtras() {

        amendMealsAndExtrasButton.perform(grey_tap())
    }

    static func tapEditGuestsForRoom1() {

        editGuestsForRoom1Button.perform(grey_tap())
    }

    static func tapAddRoom() {

        GREYCondition(name: "scroll until add room button is visible") {

            var error: NSError?

            self.addRoomButton.usingSearch(grey_scrollInDirection(.down, 300), onElementWith: grey_accessibilityID("amendTableView")).perform(grey_tap(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.2)
    }

    static func tapReviewAmends() {

        reviewAmendsButton.perform(grey_tap())
    }

    // MARK: - Validations

    // Edit Dates

    static func checkCanEditDates() {
        amendDatesButton.assert(grey_sufficientlyVisible())
    }

    // Not Amendable

    static func checkCannotAmendAndCancel() {
        amendDatesButton.assert(grey_notVisible())
        amendMealsAndExtrasButton.assert(grey_notVisible())
        addRoomButton.assert(grey_notVisible())
        cancelBookingButton.assert(grey_sufficientlyVisible())
    }

    // Amend Meals & Extras

    static func checkCanAmendMealsAndExtras() {
        amendMealsAndExtrasButton.assert(grey_sufficientlyVisible())
    }

    static func checkCannotAmendMealsAndExtras() {
        amendMealsAndExtrasButton.assert(grey_notVisible())
    }

    // Review Amends

    static func checkCanReviewAmends() {
        priceDifferenceLabel.assert(grey_sufficientlyVisible())
        reviewAmendsButton.assert(grey_sufficientlyVisible())
    }

    static func checkCannotReviewAmends() {
        priceDifferenceLabel.assert(grey_notVisible())
        reviewAmendsButton.assert(grey_notVisible())
    }
}
