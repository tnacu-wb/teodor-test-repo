//
//  HotelDetailsPage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 05/05/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import XCTest
import EarlGrey

struct HotelDetailsPage {

    // MARK: - Elements

    let backButton = EarlGrey.selectElement(with: grey_allOf([grey_buttonTitle("Back"), grey_interactable()]))
    let table = EarlGrey.selectElement(with: grey_accessibilityID("hotelDetailPageTableAcc"))
    let flexBookButton = EarlGrey.selectElement(with: grey_accessibilityID("FlexBookButton"))
    let semiFlexBookButton = EarlGrey.selectElement(with: grey_accessibilityID("Semi-FlexBookButton")).atIndex(0)
    let businessFlexBookButton = EarlGrey.selectElement(with: grey_accessibilityID("Business FlexBookButton")).atIndex(0)
    let flexBiggerRoomBookButton = EarlGrey.selectElement(with: grey_accessibilityID("FlexBiggerRoomBookButton"))

    // MARK: - Actions

    func goBack() {
        backButton.perform(grey_tap())
    }

    func tapOnFlexRate() {

        table.perform(grey_scrollInDirection(.down, 400))
        let rateDescription = "Pay now or on arrival. Cancel up to 1pm on arrival day."
        EarlGrey.selectElement(with: grey_accessibilityLabel(rateDescription)).atIndex(0).assert(grey_sufficientlyVisible())
        flexBookButton.perform(grey_tap())
    }

    func checkRoomCategory(title: String, isShown: Bool = true) {

        if isShown {
            GREYCondition(name: "Look for the room category title") {

                var error: NSError?

                EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel(title), grey_interactable()])).usingSearch(grey_scrollInDirection(.down, 100), onElementWith: grey_accessibilityID("hotelDetailPageTableAcc")).assert(grey_sufficientlyVisible(), error: &error)

                return error == nil

            }.wait(withTimeout: 10, pollInterval: 0.5)

        } else {
            EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel(title), grey_interactable()])).assert(grey_notVisible())
        }
    }

    func tapOnSemiFlexRate() {

        table.perform(grey_scrollInDirection(.down, 700))
        let rateDescription = "Pay now. Change arrival date. Cancel up to 3 days before arrival."
        EarlGrey.selectElement(with: grey_accessibilityLabel(rateDescription)).atIndex(0).assert(grey_sufficientlyVisible())
        semiFlexBookButton.perform(grey_tap())
    }

    func tapOnBusinessFlexRate() {

        table.perform(grey_scrollInDirection(.down, 200))
        businessFlexBookButton.perform(grey_tap())
    }

    func testAlertAppeared(with message: String) {

        EarlGrey.selectElement(with: grey_accessibilityLabel(message)).assert(grey_sufficientlyVisible())
    }
    func dismissAlert() {

        EarlGrey.selectElement(with: grey_accessibilityLabel("Close")).atIndex(0).perform(grey_tap())
    }
}
