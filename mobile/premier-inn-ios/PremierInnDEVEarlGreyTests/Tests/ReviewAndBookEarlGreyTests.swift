//
//  ReviewAndBookEarlGreyTests.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 18/05/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey
import Hippolyte

class ReviewAndBookEarlGreyTests: BaseEarlGreyTests {

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

            personalDetailsPage.enterTestPersonalDetails()
            personalDetailsPage.tapContinue()

            cardDetailsPage.enterTestData()
            cardDetailsPage.tapContinue()

            reviewAndBookPage.waitToLoad()
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

    func testTermsNeedToBeAccepted() {

        reviewAndBookPage.scrollDown()
        reviewAndBookPage.scrollDown()
        reviewAndBookPage.tapContinue()
        EarlGrey.selectElement(with: grey_text("Please consent to complete your booking")).assert(grey_sufficientlyVisible())

        reviewAndBookPage.goBack()
    }
}
