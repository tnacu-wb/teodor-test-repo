//
//  CardDetailsEarlGreyTests.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 15/05/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey
import Hippolyte

class CardDetailsEarlGreyTests: BaseEarlGreyTests {

    override func setUp() {
        super.setUp()

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

            personalDetailsPage.enterTestPersonalDetails()
            personalDetailsPage.tapContinue()
        }
    }

    override func tearDown() {

        cardDetailsPage.goBack()
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
        NetworkMockManager.addStubRequest(method: .GET, urlStringPattern: "https://api-uat\\.whitbread\\.co\\.uk/payment/validations/444433(.*)", jsonName: "cardValidationResponse")

        Hippolyte.shared.start()
    }

    func testInvalidCardNumber() {

        cardDetailsPage.enterCardNumber("1234123412341234")
        cardDetailsPage.checkInvalidCardError(isShown: true)
    }

    func testValidCardNumber() {

        cardDetailsPage.enterCardNumber("4444333322221111")
        cardDetailsPage.checkInvalidCardError(isShown: false)
    }

    func testPastExpiryDateError() {

        cardDetailsPage.enterCardExpiryDate("0119")
        cardDetailsPage.enterCardholderName("test")
        EarlGrey.selectElement(with: grey_accessibilityLabel("Please enter a valid expiry date")).assert(grey_sufficientlyVisible())
    }

    func testMissingCardDetailsErrors() {

        cardDetailsPage.tapContinue()
        cardDetailsPage.dismissKeyboard()
        EarlGrey.selectElement(with: grey_accessibilityLabel("Please enter a valid card number")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_accessibilityLabel("Please enter a valid expiry date")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_accessibilityLabel("This has to be between 1 and 30 characters long")).assert(grey_sufficientlyVisible())
    }

}
