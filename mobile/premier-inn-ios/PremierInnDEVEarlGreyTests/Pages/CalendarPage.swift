//
//  CalendarPage.swift
//  PremierInnUITests
//
//  Created by Georgios Aikaterinakis on 01/10/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import EarlGrey

struct CalendarPage {

    // MARK: - Elements

    static let cancelButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Cancel"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!), grey_interactable()]))
    static let doneButton = EarlGrey.selectElement(with: grey_accessibilityID("doneButton"))
    static let alertCheckInLabel = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("You can only change the check-in date"), grey_interactable()]))

    // MARK: - Actions

    static func goBack() { cancelButton.perform(grey_tap()) }

    static func tapCheckAvailability() {
        doneButton.perform(grey_tap())
    }

    static func tapContinue() {
        doneButton.perform(grey_tap())
    }

    // MARK: - Validations

    static func checkCannotCheckAvailability() {
        doneButton.assert(grey_notVisible())
    }

    static func checkCanOnlyChangeTheCheckInDate() {
        alertCheckInLabel.assert(grey_sufficientlyVisible())
        doneButton.assert(grey_notVisible())
    }
}
