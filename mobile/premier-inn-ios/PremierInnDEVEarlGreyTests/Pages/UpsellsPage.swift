//
//  UpsellsPage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 06/05/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import XCTest
import EarlGrey

public struct UpsellsPage {

    // MARK: - Types

    enum Meal: String {
        case PremierInnBreakfast = "Premier Inn Breakfast"
        case ContinentalBreakfast = "Continental Breakfast"
        case MealDeal = "Meal Deal (Breakfast & Dinner)"
        case NoMeals = "No meals"
    }

    enum Extra: String {
        case UltimateWiFi = "Upgrade to Ultimate Wi-Fi"
        case FreeWiFi = "Free Wi-Fi"
    }

    // MARK: - Elements
    
    static let backButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Hotel"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    static let continueButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Continue"), grey_kindOfClass(NSClassFromString("UIButton")!)]))

    // Options
    static let firstBreakfastOption = EarlGrey.selectElement(with: grey_accessibilityID("breakfastRadioBtnAcc0"))
    static let firstWifiOption = EarlGrey.selectElement(with: grey_accessibilityID("wifiRadioBtnAcc0"))

    // MARK: - Actions

    // Selection

    static func addMeal(_ meal: Meal) {

        GREYCondition(name: "scroll until meal is visible") {

            var error: NSError?

            let breakfastOption = EarlGrey.selectElement(with: grey_accessibilityLabel(meal.rawValue))
            breakfastOption.usingSearch(grey_scrollInDirection(.down, 200), onElementWith: grey_accessibilityID("tableView")).perform(grey_tap(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.5)
    }

    static func addExtra(_ extra: Extra) {

        GREYCondition(name: "scroll until wifi is visible") {

            var error: NSError?

            let wifiOption = EarlGrey.selectElement(with: grey_accessibilityLabel(extra.rawValue))
            wifiOption.usingSearch(grey_scrollInDirection(.down, 300), onElementWith: grey_accessibilityID("tableView")).perform(grey_tap(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.5)
    }

    static func addBreakfast(_ item: Int = 0) {

        GREYCondition(name: "scroll until wifi is visible") {

            var error: NSError?

            let breakfastOption = EarlGrey.selectElement(with: grey_accessibilityID("breakfastRadioBtnAcc\(item)"))
            breakfastOption.usingSearch(grey_scrollInDirection(.down, 300), onElementWith: grey_accessibilityID("tableView")).perform(grey_tap(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.5)
    }

    static func addWifi(_ item: Int = 0) {

        GREYCondition(name: "scroll until wifi is visible") {

            var error: NSError?

            let wifiOption = EarlGrey.selectElement(with: grey_accessibilityID("wifiRadioBtnAcc\(item)"))
            wifiOption.usingSearch(grey_scrollInDirection(.down, 300), onElementWith: grey_accessibilityID("tableView")).perform(grey_tap(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.5)
    }

    // Navigation

    static func goBack() { backButton.perform(grey_tap()) }

    static func tapContinue() { continueButton.perform(grey_tap()) }

    static func checkUpsellsVisible(with name: String) {

        GREYCondition(name: "check upsell visible") {

            var error: NSError?
            EarlGrey.selectElement(with: grey_accessibilityLabel(name)).usingSearch(grey_scrollInDirection(.down, 100), onElementWith: grey_accessibilityID("tableView")).assert(grey_sufficientlyVisible(), error: &error)

            return error == nil
        }.wait(withTimeout: 5, pollInterval: 0.5)
    }
}
