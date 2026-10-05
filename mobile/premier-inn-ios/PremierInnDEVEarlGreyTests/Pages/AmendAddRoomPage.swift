//
//  AmendAddRoomPage.swift
//  PremierInnUITests
//
//  Created by Georgios Aikaterinakis on 29/09/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import EarlGrey

struct AmendAddRoomPage {

    // MARK: - Elements

    static let cancelButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Cancel"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!), grey_interactable()]))
    static let titleButton = EarlGrey.selectElement(with: grey_accessibilityID("Title"))
    static let firstNameTextfield = EarlGrey.selectElement(with: grey_accessibilityID("firstNameAcc"))
    static let lastNameTextfield = EarlGrey.selectElement(with: grey_accessibilityID("lastNameAcc"))
    static let keyboardDoneButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Done"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!), grey_interactable()]))
    static let continueButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Continue"))
    static let checkAvailabilityButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Check availability"))

    // MARK: - Actions

    static func goBack() { cancelButton.perform(grey_tap()) }
    static func tapCheckAvailability() { checkAvailabilityButton.perform(grey_tap()) }
    static func enterTestGuestDetails() {

        titleButton.perform(grey_tap())
        EarlGrey.selectElement(with: grey_accessibilityLabel("Mr")).perform(grey_tap())
        firstNameTextfield.perform(grey_typeText("T"))
        lastNameTextfield.perform(grey_typeText("Test"))
        dismissKeyboard()
    }
    static func dismissKeyboard() { keyboardDoneButton.perform(grey_tap()) }
    static func tapContinue() { continueButton.perform(grey_tap()) }

    // MARK: Validations

    static func checkAvailabilityButtonIsShown() {
        checkAvailabilityButton.assert(grey_sufficientlyVisible())
    }
}
