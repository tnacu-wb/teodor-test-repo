//
//  SearchResultsPage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 22/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey

@testable import PremierInn

class SearchResultsPage {

    private let editSearch = EarlGrey.selectElement(with: grey_accessibilityID("editSearch"))
    private let searchResultMapViewButton = EarlGrey.selectElement(with: grey_accessibilityID("searchResultMapViewButton"))
    private let navigateUpButton = EarlGrey.selectElement(with: grey_accessibilityID("NavigateUp"))
    private let venuesList = EarlGrey.selectElement(with: grey_accessibilityID(AccessibilityIdentifiers.SearchResults.venuesList))

    func checkPageElementsExist() {

        if UIDevice.current.userInterfaceIdiom != .pad {
            editSearch.assert(grey_sufficientlyVisible())
            navigateUpButton.assert(grey_sufficientlyVisible())
        }
    }

    func tapHotelInList(hotelName: String) {

        venuesList.assert(grey_sufficientlyVisible())

        let maxNumberOfVenuesIndexes: [Int] = Array(0...39)
        let venueLabelIDs: [GREYMatcher] = maxNumberOfVenuesIndexes.map { return String(format: AccessibilityIdentifiers.SearchResults.hotelNameFormat, $0) }.map { return grey_accessibilityID($0)}

        var error: NSError?
        EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel(hotelName), grey_anyOf(venueLabelIDs)])).usingSearch(grey_scrollInDirection(.down, 400), onElementWith: grey_accessibilityID(AccessibilityIdentifiers.SearchResults.venuesList)).perform(grey_tap(), error: &error)

        if error?.domain == kGREYInteractionErrorDomain {

            // did not find hotel in search results
            print(error?.localizedDescription as Any)
        }
    }

    func goBack() { navigateUpButton.perform(grey_tap()) }
}
