//
//  SearchResultsEarlGreyTests.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 22/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey
import Hippolyte

class SearchResultsEarlGreyTests: BaseEarlGreyTests {

    override func setUp() {
        super.setUp()

        setupMockAPI()
    }

    override func tearDown() {

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

//        https://api-uat.whitbread.co.uk/booking/hotels/LIVLIM/availability?adults=1&arrival=2020-08-27&bookingChannel=MOBILE&children=0&cot=false&country=gb&departure=2020-08-28&language=en&rooms=1&type=DB&upsellFormat=PH

//        https://api-uat.whitbread.co.uk/snowdrop/search/hotels/availabilities?adults=1&arrival=2020-04-29&bookingChannel=MOBILE&children=0&cot=false&country=gb&departure=2020-04-30&language=en&location=ChIJt2BwZIrfekgRAW4XP28E3EI&locationFormat=PLACEID&radius=50&radiusUnit=MILES&rooms=1&size=40&sort=DISTANCE&sort=AVAILABLE_FIRST&type=DB&upsellFormat=NO

        Hippolyte.shared.start()
    }

    func testSearchWithDefaultCriteria() {

        homePage.tapSearch()
        // check SRP elements
        searchResultsPage.checkPageElementsExist()

        searchResultsPage.goBack()
    }

    func testSearchHotelsInLiverpool() {

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
            reviewAndBookPage.acceptTerms()

            reviewAndBookPage.goBack()
            cardDetailsPage.goBack()
            personalDetailsPage.goBack()
            UpsellsPage.goBack()
            hotelDetailsPage.goBack()
            searchResultsPage.goBack()
        }
    }
}
