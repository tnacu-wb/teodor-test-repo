//
//  RoomUpsellEarlGreyTests.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 14/07/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey
import Hippolyte

class RoomUpsellEarlGreyTests: BaseEarlGreyTests {

    // MARK: Boilerplate

    override func setUp() {
        super.setUp()

        setupMockAPI()
    }

    override func tearDown() {

        hotelDetailsPage.goBack()
        searchResultsPage.goBack()

        super.tearDown()
    }

    // MARK: Config

    override func setupMockAPI() {

        Hippolyte.shared.start()
    }

    private func setupAvailabilityStub(using mockConfig: MockConfig) {

        Hippolyte.shared.clearStubs()

        loadMock(with: SuggestionMock.liverpool)
        loadMock(with: PlacesLookupMock.liverpool)
        loadMock(with: AvailabilitiesMock.liverpool)
        loadMock(with: mockConfig)
        loadMock(with: HotelInfoMock.liverpool)
        loadMock(with: HotelReviewsMock.liverpool)
    }

    // MARK: Tests

    func testStandardRooms() {

        setupAvailabilityStub(using: AvailabilityMock.standardRooms)

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.checkRoomCategory(title: "Standard rooms")
    }

    func testPremierPlusRooms() {

        setupAvailabilityStub(using: AvailabilityMock.premierPlusRooms)

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.checkRoomCategory(title: "Standard rooms")
        hotelDetailsPage.checkRoomCategory(title: "Premier Plus rooms")
    }

    func testBusinessRooms() {

        setupAvailabilityStub(using: AvailabilityMock.businessRooms)

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.checkRoomCategory(title: "Standard rooms")
        hotelDetailsPage.checkRoomCategory(title: "Business rooms")
    }

    func testMixedRooms() {

        setupAvailabilityStub(using: AvailabilityMock.mixedRooms)

        givenIHaveSelectedAccessibleRoomCriteria()
        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.checkRoomCategory(title: "Standard rooms", isShown: false)
    }
}
