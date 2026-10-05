//
//  EditRoomPage.swift
//  PremierInnUITests
//
//  Created by Filippo Minelle on 15/10/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import XCTest
import EarlGrey

struct EditRoomPage {

    // MARK: - Elements

    // static let backButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("amendBackButton"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    static let cancelButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("cancelBarButtonItem")]))
    static let continueButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("confirmButton")]))
    static let changeButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("changeLabel")]))

    // MARK: - Actions

    static func goBack() {
        cancelButton.perform(grey_tap())
    }

    static func tapContinue() {
        continueButton.perform(grey_tap())
    }

    static func tapChange() {
        changeButton.perform(grey_tap())
    }

    // MARK: - Validations

    static func checkCanChangeLeadGuest() {
        changeButton.assert(grey_sufficientlyVisible())
    }

    static func checkCannotChangeLeadGuest() {
        changeButton.assert(grey_notVisible())
    }
}

