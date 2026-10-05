//
//  MyAccountPage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 17/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey

@testable import PremierInn

struct MyAccountPage {

    // MARK: - Elements

    static let searchTabBarItem = EarlGrey.selectElement(with: grey_accessibilityID("searchTabBarItem"))
    static let bookingsTabBarItem = EarlGrey.selectElement(with: grey_accessibilityID("bookingsTabBarItem"))

    static let loginButton = EarlGrey.selectElement(with: grey_accessibilityID(AccessibilityIdentifiers.Account.loginButton))
    static let personalDetailsRow = EarlGrey.selectElement(with: grey_accessibilityLabel("Personal details"))
    static let paymentMethodsRow = EarlGrey.selectElement(with: grey_accessibilityLabel("Payment methods"))
    static let logoutButton = EarlGrey.selectElement(with: grey_accessibilityID(AccessibilityIdentifiers.Account.logoutButton))
    static let table = EarlGrey.selectElement(with: grey_accessibilityID(AccessibilityIdentifiers.Account.tableView))

    static let covid19LinkButton = EarlGrey.selectElement(with: grey_accessibilityLabel("COVID 19: Premier Inn Updates"))

    // MARK: - Actions

    static func goToHome() {
        GREYCondition(name: "wait for tab bar item, might be coming from another screen") {

            var error: NSError?

            self.searchTabBarItem.perform(grey_tap(), error: &error)

            return error == nil
        }.wait(withTimeout: 5, pollInterval: 0.5)
        //searchTabBarItem.perform(grey_tap())
    }

    static func goToMyBookings() {
        bookingsTabBarItem.perform(grey_tap())
    }

    static func tapLogin() {

        loginButton.perform(grey_tap())
    }

    static func tapPersonalDetails() {

        personalDetailsRow.perform(grey_tap())
    }

    static func tapPaymentMethods() {

        paymentMethodsRow.perform(grey_tap())
    }

    static func logout() {

        var error: NSError?
        logoutButton.usingSearch(grey_scrollInDirection(.down, 170), onElementWith: grey_accessibilityID(AccessibilityIdentifiers.Account.tableView)).perform(grey_tap(), error: &error)

        if error?.domain == kGREYInteractionErrorDomain && error?.localizedDescription == "Interaction cannot continue because the desired element was not found." {

            // already logged out
        }
    }

    static func tapCovid19Link() {

        covid19LinkButton.assert(grey_sufficientlyVisible())
        covid19LinkButton.perform(grey_tap())
    }
}
