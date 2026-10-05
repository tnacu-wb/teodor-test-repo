//
//  BathroomSelectionEarlGreyTests.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 15/07/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey
import Hippolyte

class BathroomSelectionEarlGreyTests: BaseEarlGreyTests {

    // MARK: Boilerplate

    override func setUp() {
        super.setUp()

        setupMockAPI()
    }

    override func tearDown() {

        BathroomSelectionPage.goBack()
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

    func test1RoomWetRoomAndLoweredBathWithAvailability() {

        setupAvailabilityStub(using: AvailabilityMock.accessibleRoom1)

        givenIHaveSelectedAccessibleRoomCriteria()
        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnFlexRate()

        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "LoweredBathTitle1", isShown: true)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "WetRoomTitle1", isShown: true)
    }

    func test2RoomsWetRoomAndLoweredBathWithAvailability() {

        setupAvailabilityStub(using: AvailabilityMock.accessibleRooms2)

        givenIHaveSelectedAccessibleRoomCriteria()
        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnFlexRate()

        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "LoweredBathTitle1", isShown: true)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "WetRoomTitle1", isShown: true)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "LoweredBathTitle2", isShown: true)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "WetRoomTitle2", isShown: true)
    }

    func test3RoomsWetRoomAndLoweredBathWithExactAvailability() {

        setupAvailabilityStub(using: AvailabilityMock.accessibleRoomsMixedExact)

        givenIHaveSelectedAccessibleRoomCriteria()
        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnFlexRate()

        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "LoweredBathTitle1", isShown: true)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "WetRoomTitle1", isShown: false)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "NoMoreWetRoom1", isShown: true)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "LoweredBathTitle2", isShown: false)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "WetRoomTitle2", isShown: true)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "NoMoreLoweredBath2", isShown: true)
    }

    func test3RoomsWetRoomAndLoweredBathWithLimitedAvailability() {

        setupAvailabilityStub(using: AvailabilityMock.accessibleRoomsMixedLimited)

        givenIHaveSelectedAccessibleRoomCriteria()
        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnFlexRate()

        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "LoweredBathTitle1", isShown: true)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "WetRoomTitle1", isShown: true)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "NoMoreLoweredBath2", isShown: false)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "LoweredBathTitle2", isShown: true)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "WetRoomTitle2", isShown: true)

        EarlGrey.selectElement(with: grey_accessibilityID("WetRoomRadioButton1")).perform(grey_tap())
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "NoMoreLoweredBath2", isShown: true)
    }

    func test3RoomsWithUnknownAccessibleRoomType() {

        setupAvailabilityStub(using: AvailabilityMock.accessibleRoomsUnknown)

        givenIHaveSelectedAccessibleRoomCriteria()
        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnFlexRate()

        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "LoweredBathTitle1", isShown: true)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "WetRoomTitle1", isShown: false)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "LoweredBathTitle2", isShown: true)
        BathroomSelectionPage.checkElementInBathroomSelection(accessibilityID: "WetRoomTitle2", isShown: false)
    }

    func test3RoomsMixed_standardRoomMessage() {

        setupAvailabilityStub(using: AvailabilityMock.accessibleRoomsMixed)

        givenIHaveSelectedAccessibleRoomCriteria()
        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnFlexRate()

        BathroomSelectionPage.standardRoomMessage(isShown: true)
    }
}
