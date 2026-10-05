//
//  PersonalDetailsEarlGreyTests.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 13/05/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey
import Hippolyte

class PersonalDetailsEarlGreyTests: BaseEarlGreyTests {

    override func setUp() {
        super.setUp()

        setupMockAPI()

        homePage.goToLocationSelection()
        locationSuggestionsPage.searchLocation(text: "Liverpool")
        locationSuggestionsPage.selectSuggestion(text: "Liverpool, UK") {

            homePage.tapSearch()

            // check SRP elements
            searchResultsPage.checkPageElementsExist()
            searchResultsPage.tapHotelInList(hotelName: "Liverpool City Centre (Moorfields)")

            hotelDetailsPage.tapOnFlexRate()

            UpsellsPage.addBreakfast()
            UpsellsPage.tapContinue()
        }
    }

    override func tearDown() {

        personalDetailsPage.goBack()
        UpsellsPage.goBack()
        hotelDetailsPage.goBack()
        searchResultsPage.goBack()

        super.tearDown()
    }

    override func setupMockAPI() {

        NetworkMockManager.addStubRequest(method: .GET, urlStringPattern: "https://api-uat\\.whitbread\\.co\\.uk/autocomplete(.*)", jsonName: "liverpoolSuggestionsResponse")
        NetworkMockManager.addStubRequest(method: .GET, urlStringPattern: "https://maps\\.googleapis\\.com/maps/api/place/details/json(.*)", jsonName: "liverpoolGooglePlacesResponse")
        NetworkMockManager.addStubRequest(method: .GET, urlStringPattern: "https://api-uat\\.whitbread\\.co\\.uk/snowdrop/search/hotels/availabilities(.*)", jsonName: "liverpoolAvailabilitiesResponse")
        NetworkMockManager.addStubRequest(method: .GET, urlStringPattern: "https://api-uat\\.whitbread\\.co\\.uk/booking/hotels/(.*)/availability(.*)", jsonName: "availabilityResponse")
        NetworkMockManager.addStubRequest(method: .GET, urlString: "https://api-uat.whitbread.co.uk/hotels/LIVCIT", jsonName: "liverpoolHotelInfo")
        NetworkMockManager.addStubRequest(method: .GET, urlString: "https://api-uat.whitbread.co.uk/reviews/LIVCIT?lang=en_US", jsonName: "reviewResponse")
        NetworkMockManager.holdBookingStubRequest()

        NetworkMockManager.addStubRequest(method: .GET, urlString: "https://api-uat.whitbread.co.uk/postcodes/SW12AB/addresses?business=false", jsonName: "successfulAddressLookupResponse")
        NetworkMockManager.addStubRequest(method: .GET, urlString: "https://api-uat.whitbread.co.uk/postcodes/SW12AB/addresses/id1", jsonName: "successfulAddressDetailsResponse")

        Hippolyte.shared.start()
    }

    func testAddressLookupSuccess() {

        personalDetailsPage.scrollDown()
        personalDetailsPage.findPostcode("SW12AB")
        addressLookupPage.selectAddress("line 1, line 2, town, SW1 2AB")
        // assert address details are in the textfields
        personalDetailsPage.checkFieldsArePopulatedWithAddress(postcode: "SW1 2AB", line1: "line 1", line2: "line 2", line3: "")
    }

    func testAddressLookupNoResults() {

        personalDetailsPage.scrollDown()
        personalDetailsPage.findPostcode("SE11AA")
        addressLookupPage.goBack()
    }

    func testMissingPersonalDetails() {

        personalDetailsPage.tapContinue()
        personalDetailsPage.requiredValuesErrors()
    }
}
