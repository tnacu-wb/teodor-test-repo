//
//  LocationSuggestionsPage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 22/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey

class LocationSuggestionsPage {

    private let searchTextField = EarlGrey.selectElement(with: grey_accessibilityID("searchTextField"))
    private let searchCancelButton = EarlGrey.selectElement(with: grey_accessibilityID("searchCancelButton"))

    func dismiss() { searchCancelButton.perform(grey_tap()) }
    func searchLocation(text: String) { searchTextField.perform(grey_typeText(text)) }
    func selectSuggestion(text: String, completion: () -> Void) {

        GREYCondition(name: "Wait for the suggestions result") {

            var error: NSError?

            EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel(text), grey_interactable()])).usingSearch(grey_scrollInDirection(.down, 100), onElementWith: grey_accessibilityID("suggestionsTable")).perform(grey_tap(), error: &error)

            return error == nil

        }.wait(withTimeout: 10, pollInterval: 0.5)

        completion()
    }
}
