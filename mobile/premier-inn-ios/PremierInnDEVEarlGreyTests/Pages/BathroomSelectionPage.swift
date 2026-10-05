//
//  BathroomSelectionPage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 15/07/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import EarlGrey

struct BathroomSelectionPage {

    // MARK: - Elements

    static let backButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Hotel"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    static let continueButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Continue"))
    static let standardRoomMessage = EarlGrey.selectElement(with: grey_accessibilityLabel("no bathroom selection needed for this room"))

    // MARK: - Actions

    static func goBack() { backButton.perform(grey_tap()) }
    static func tapContinue() { continueButton.perform(grey_tap()) }

    // MARK: Validations

    static func checkElementInBathroomSelection(accessibilityID: String, isShown: Bool) {

        if isShown {
            GREYCondition(name: "Look for the accessible room title") {

                var error: NSError?

                EarlGrey.selectElement(with: grey_accessibilityID(accessibilityID)).usingSearch(grey_scrollInDirection(.down, 100), onElementWith: grey_accessibilityID("tableView")).assert(grey_sufficientlyVisible(), error: &error)

                return error == nil

            }.wait(withTimeout: 10, pollInterval: 0.5)

        } else {
            EarlGrey.selectElement(with: grey_accessibilityID(accessibilityID)).assert(grey_notVisible())
        }
    }

    static func standardRoomMessage(isShown: Bool) {

        GREYCondition(name: "Look for standard room message") {

            var error: NSError?

            standardRoomMessage.usingSearch(grey_scrollInDirection(.down, 100), onElementWith: grey_accessibilityID("tableView")).assert(isShown ? grey_sufficientlyVisible() : grey_notVisible(), error: &error)

            return error == nil

        }.wait(withTimeout: 10, pollInterval: 0.5)
    }
}
