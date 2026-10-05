//
//  HomePage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 16/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey

class HomePage {

    private let homePageGDPRAcceptAcc = EarlGrey.selectElement(with: grey_accessibilityID("homePageGDPRAcceptAcc"))
    private let ctaButton = EarlGrey.selectElement(with: grey_accessibilityID("ctaButton"))
    private let dismissBannerButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Dismiss"))

    private let piLogo = EarlGrey.selectElement(with: grey_accessibilityID("PI-logo"))
    private let homePageTitle = EarlGrey.selectElement(with: grey_accessibilityID("homePageTitle"))
    private let searchSuggestionTitle = EarlGrey.selectElement(with: grey_accessibilityID("searchSuggestionTitle"))
    private let datesTitle = EarlGrey.selectElement(with: grey_accessibilityID("datesTitle"))
    private let roomsTitle = EarlGrey.selectElement(with: grey_accessibilityID("roomsTitle"))
    private let searchButton = EarlGrey.selectElement(with: grey_accessibilityID("searchButton"))

    private let searchTabBarItem = EarlGrey.selectElement(with: grey_accessibilityID("searchTabBarItem"))
    private let bookingsTabBarItem = EarlGrey.selectElement(with: grey_accessibilityID("bookingsTabBarItem"))
    private let accountTabBarItem = EarlGrey.selectElement(with: grey_accessibilityID("accountTabBarItem"))
    private let versionNumberLabel = EarlGrey.selectElement(with: grey_accessibilityID("versionNumberAcc"))

//    private let debug = XCUIApplication().otherElements["Debug"]

    func hideBanner() {

        var error: NSError?

        homePageGDPRAcceptAcc.assert(grey_sufficientlyVisible(), error: &error)
        if error?.domain == kGREYInteractionErrorDomain && error?.localizedDescription == "Interaction cannot continue because the desired element was not found." {

            // already dismissed gdpr screen
        } else {
            homePageGDPRAcceptAcc.perform(grey_tap())
            homePageGDPRAcceptAcc.assert(grey_notVisible())
        }

        error = nil
        ctaButton.assert(grey_sufficientlyVisible(), error: &error)
        if error?.domain == kGREYInteractionErrorDomain && error?.localizedDescription == "Interaction cannot continue because the desired element was not found." {

            // already dismissed important accouncements screen
        } else {
            ctaButton.perform(grey_tap())
            ctaButton.assert(grey_notVisible())
        }

        error = nil
        dismissBannerButton.assert(grey_sufficientlyVisible(), error: &error)
        if error?.domain == kGREYInteractionErrorDomain && error?.localizedDescription == "Interaction cannot continue because the desired element was not found." {

            // already dismissed covid banner
        } else {
            dismissBannerButton.perform(grey_tap())
            dismissBannerButton.assert(grey_notVisible())
        }
    }

    func checkPageElementsExist() {

        homePageTitle.assert(grey_sufficientlyVisible())
        searchSuggestionTitle.assert(grey_sufficientlyVisible())
        datesTitle.assert(grey_sufficientlyVisible())
        roomsTitle.assert(grey_sufficientlyVisible())
        searchButton.assert(grey_sufficientlyVisible())
        searchTabBarItem.assert(grey_sufficientlyVisible())
        bookingsTabBarItem.assert(grey_sufficientlyVisible())
        accountTabBarItem.assert(grey_sufficientlyVisible())
        versionNumberLabel.assert(grey_sufficientlyVisible())
    }

    func select(environment: BartEnvironment) {

        versionNumberLabel.perform(grey_tap())

        let debugPage = DebugPage()
        debugPage.select(environment: environment)
    }

    func goToSearch() { searchTabBarItem.perform(grey_tap()) }
    func goToMyBookings() { bookingsTabBarItem.perform(grey_tap()) }
    func goToMyAccount() { accountTabBarItem.perform(grey_tap()) }
    func goToLocationSelection() { searchSuggestionTitle.perform(grey_tap()) }
    func tapSearch() { searchButton.perform(grey_tap()) }
    func goToRoomCriteria() { roomsTitle.perform(grey_tap()) }

    func checkNameAppeared(for company: String) {

        EarlGrey.selectElement(with: grey_accessibilityLabel(company)).assert(grey_sufficientlyVisible())
    }
}
