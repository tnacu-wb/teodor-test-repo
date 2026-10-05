//
//  UpsellsEarlGreyTests+BB.swift
//  PremierInnUITests
//
//  Created by Freddie Parks on 18/11/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey
import Hippolyte

private struct MockPack {
    var login: LoginMock = .selfWhitbreadBB
    var userMock: GetUserMock = .selfWhitbreadBB
    var company: GetCompanyMock = .company2891
    var suggestions: SuggestionMock = .liverpool
    var places: PlacesLookupMock = .liverpool
    var availabilities: AvailabilitiesMock = .liverpool
    var availability: AvailabilityMock = .liverpoolBB
    var hotel: HotelInfoMock = .liverpool
    var reviews: HotelReviewsMock = .liverpool
    var hold: HoldBookingMock = .success
}

class UpsellsEarlGreyTestsBB: BaseEarlGreyTests {

    // MARK: Boilerplate

    override func setUp() {
        super.setUp()

        setupMockAPI()
    }

    override func tearDown() {

        super.tearDown()
    }

    // MARK: Config

    override func setupMockAPI() {

        Hippolyte.shared.start()
    }

    private func load(mockPack: MockPack) {

        Hippolyte.shared.clearStubs()

        loadMock(with: mockPack.login)
        loadMock(with: mockPack.userMock)
        loadMock(with: mockPack.company)
        loadMock(with: mockPack.suggestions)
        loadMock(with: mockPack.places)
        loadMock(with: mockPack.availabilities)
        loadMock(with: mockPack.availability)
        loadMock(with: mockPack.hotel)
        loadMock(with: mockPack.reviews)
        loadMock(with: mockPack.hold)
    }

    // MARK: Tests

    func testBBOnlyWifiUpsellAvailableInRateAllCompanyAllowancesActive() {

        load(mockPack: MockPack())

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.goToHome()

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnBusinessFlexRate()

        UpsellsPage.checkUpsellsVisible(with: "Upgrade to Ultimate Wi-Fi ")

        UpsellsPage.goBack()
        hotelDetailsPage.goBack()
        searchResultsPage.goBack()
    }

    func testBBOnlyPIBreakfastUpsellAvailableInRateAllCompanyAllowancesActive() {

        var mockPack = MockPack()
        mockPack.availability = .liverpoolBBPoAOnlyPremierInnBreak
        load(mockPack: mockPack)

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.goToHome()

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnBusinessFlexRate()

        UpsellsPage.checkUpsellsVisible(with: "Premier Inn Breakfast")

        UpsellsPage.goBack()
        hotelDetailsPage.goBack()
        searchResultsPage.goBack()
    }

    func testBBOnlyPIBreakfastUpsellAvailableInRateNoCompanyAllowancesActive() {

        var mockPack = MockPack()
        mockPack.company = .company2891NoUpsellsAllowed
        mockPack.availability = .liverpoolBBPoAOnlyPremierInnBreak
        load(mockPack: mockPack)

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.goToHome()

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnBusinessFlexRate()

        businessCardQuestionsPage.goBack()
        hotelDetailsPage.goBack()
        searchResultsPage.goBack()
    }
}
