//
//  CheckInOnlinePage.swift
//  PremierInnUITests
//
//  Created by Freddie Parks on 02/09/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import XCTest
import EarlGrey

class CheckInOnlinePage {

    private let backButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("My bookings"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    private let checkInButton = EarlGrey.selectElement(with: grey_accessibilityID("checkInButton"))
    private let continueToPaymentButton = EarlGrey.selectElement(with: grey_accessibilityID("paymentButton"))

    func goBack() {
        backButton.perform(grey_tap())
    }

    func tapCheckIn() {
        checkInButton.perform(grey_tap())
    }

    func tapContinueToPayment() {
        continueToPaymentButton.perform(grey_tap())
    }

    func checkCheckInPossible() {
        checkInButton.assert(grey_sufficientlyVisible())
    }

    func checkPaymentRequired() {
        continueToPaymentButton.assert(grey_sufficientlyVisible())
    }

    func waitForAnimations() {
        GREYCondition(name: "wait for animations") {
            return true
        }.wait(withTimeout: 10)
    }

    func checkCheckedInIsShown() {
        GREYCondition(name: "Look for the checked in copy") {

            var error: NSError?

            EarlGrey.selectElement(with: grey_accessibilityLabel("You have checked-in")).assert(grey_sufficientlyVisible(), error: &error)

            return error == nil

        }.wait(withTimeout: 10, pollInterval: 0.5)
    }
}
