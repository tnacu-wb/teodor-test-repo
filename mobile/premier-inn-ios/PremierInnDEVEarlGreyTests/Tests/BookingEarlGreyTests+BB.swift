//
//  BookingEarlGreyTests+BB.swift
//  PremierInnUITests
//
//  Created by Freddie Parks on 11/11/2020.
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
    var booking: BookingMock = .payOnArrival
    var payment: PaymentMock = .successNo3DS
}

class BookingEarlGreyTestsBB: BaseEarlGreyTests {

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
        loadMock(with: mockPack.booking)
        loadMock(with: mockPack.payment)
    }

    private func setupPayNowStubs(using paymentMock: PaymentMock, with cardMock: CardValidationMock = .visaCredit) {

        var mockPack = MockPack()
        mockPack.booking = .payNow
        mockPack.payment = paymentMock

        load(mockPack: mockPack)
    }

    // MARK: - Tests

    func testCompanyNameAndCanSelectBFlex() {

        load(mockPack: MockPack())

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.goToHome()

        homePage.checkNameAppeared(for: "Vcp Europa Ltd")

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnBusinessFlexRate()

        UpsellsPage.goBack()
        hotelDetailsPage.goBack()
        searchResultsPage.goBack()
    }

    func testNoBBCardsAndCantPayWithPersonal() {

        var mockPack = MockPack()
        mockPack.company = .company2891NoCards
        load(mockPack: mockPack)

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.goToHome()

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnBusinessFlexRate()
        hotelDetailsPage.testAlertAppeared(with: "You do not have permissions to make bookings, or access to a centrally stored card. Please contact your Travel Manager to request that they make a booking for you, or assign a card to you.")
        hotelDetailsPage.dismissAlert()

        hotelDetailsPage.goBack()
        searchResultsPage.goBack()
    }

    func testStayerCantBook() {

        var mockPack = MockPack()
        mockPack.userMock = .stayerBB
        load(mockPack: mockPack)

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.goToHome()

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnBusinessFlexRate()
        hotelDetailsPage.testAlertAppeared(with: "Please contact your travel manager to request a booking to be made for you.")
        hotelDetailsPage.dismissAlert()

        hotelDetailsPage.goBack()
        searchResultsPage.goBack()
    }

    func testCantBookPrepaymentRequiredWithoutBACNonManager() {

        var mockPack = MockPack()
        mockPack.company = .company2891NoCentralBACs
        mockPack.availability = .liverpoolBBPrepaymentRequired
        load(mockPack: mockPack)

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.goToHome()

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnBusinessFlexRate()
        hotelDetailsPage.testAlertAppeared(with: "Company cards are not eligible for this rate type. Please select another rate or contact your Travel Manager to change your card type.")
        hotelDetailsPage.dismissAlert()

        hotelDetailsPage.goBack()
        searchResultsPage.goBack()
    }

    func testCantBookPrepaymentRequiredWithoutBACManager() {

        var mockPack = MockPack()
        mockPack.userMock = .managerBB
        mockPack.company = .company2891NoCentralBACs
        mockPack.availability = .liverpoolBBPrepaymentRequired
        load(mockPack: mockPack)

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.goToHome()

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnBusinessFlexRate()
        hotelDetailsPage.testAlertAppeared(with: "Company cards are not eligible for this rate type. Please select another rate or visit the Premier Inn Business website to change your card type.")
        hotelDetailsPage.dismissAlert()

        hotelDetailsPage.goBack()
        searchResultsPage.goBack()
    }

    func testManagerCanBookPrepaymentCSCNotBAC() {

        var mockPack = MockPack()
        mockPack.userMock = .managerBB
        mockPack.company = .company2891NoBACInCSCAllowPersonal
        mockPack.availability = .liverpoolBBPrepaymentRequired
        load(mockPack: mockPack)

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.goToHome()

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnBusinessFlexRate()
        UpsellsPage.tapContinue()
        businessCardQuestionsPage.tapContinue()
        reviewAndBookPage.scrollDown(amount: 250)
        reviewAndBookPage.checkPayingWithCardNumber(withLastFourDigits: "1111")

        reviewAndBookPage.changePaymentMethodButtonAcc.perform(grey_tap())
        paymentMethodsPage.checkDisabledCard(withCardName: "twat")
        paymentMethodsPage.checkCardInfo(showing: "Company cards are not eligible for this rate.")
        paymentMethodsPage.checkSelectableCard(withCardName: "VI")
        paymentMethodsPage.cancel()

        reviewAndBookPage.goBack()
        businessCardQuestionsPage.goBack()
        UpsellsPage.goBack()
        hotelDetailsPage.goBack()
        searchResultsPage.goBack()
    }

    func testManagerCanBookPrepaymentCSCBAC() {

        var mockPack = MockPack()
        mockPack.userMock = .managerBB
        mockPack.availability = .liverpoolBBPrepaymentRequired
        load(mockPack: mockPack)

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.goToHome()

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnBusinessFlexRate()
        UpsellsPage.tapContinue()
        businessCardQuestionsPage.tapContinue()
        reviewAndBookPage.scrollDown(amount: 250)
        reviewAndBookPage.checkPayingWithCardNumber(withLastFourDigits: "1111")

        reviewAndBookPage.changePaymentMethodButtonAcc.perform(grey_tap())
        paymentMethodsPage.checkSelectableCard(withCardName: "bac")
        paymentMethodsPage.checkCardInfo(showing: "To update your card, please visit the Premier Inn Business website.")
        paymentMethodsPage.checkSelectableCard(withCardName: "VI")
        paymentMethodsPage.cancel()

        reviewAndBookPage.goBack()
        businessCardQuestionsPage.goBack()
        UpsellsPage.goBack()
        hotelDetailsPage.goBack()
        searchResultsPage.goBack()
    }

    func testCanBookPrepaymentCSCNotBACNoPersonalCard() {

        var mockPack = MockPack()
        mockPack.userMock = .managerBBNoPersonalCard
        mockPack.company = .company2891NoBACInCSCAllowPersonal
        mockPack.availability = .liverpoolBBPrepaymentRequired
        load(mockPack: mockPack)

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.goToHome()

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnBusinessFlexRate()
        UpsellsPage.tapContinue()
        businessCardQuestionsPage.tapContinue()

        paymentDetailsPage.checkSaveCard(is: false)
        paymentDetailsPage.goBack(with: "Additional Info")

        businessCardQuestionsPage.goBack()
        UpsellsPage.goBack()
        hotelDetailsPage.goBack()
        searchResultsPage.goBack()
    }

    func testCanBookPrepaymentCSC() {

        var mockPack = MockPack()
        mockPack.userMock = .managerBB
        mockPack.company = .company2891
        mockPack.availability = .liverpoolBBPrepaymentRequired
        load(mockPack: mockPack)

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.goToHome()

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnBusinessFlexRate()
        UpsellsPage.tapContinue()
        businessCardQuestionsPage.tapContinue()
        reviewAndBookPage.scrollDown(amount: 250)
        reviewAndBookPage.checkPayingWithCardNumber(withLastFourDigits: "5041")

        reviewAndBookPage.goBack()
        businessCardQuestionsPage.goBack()
        UpsellsPage.goBack()
        hotelDetailsPage.goBack()
        searchResultsPage.goBack()
    }

    func testPaymentMethodsUpdateCardMessageNonManager() {

        load(mockPack: MockPack())

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.tapPaymentMethods()

        paymentMethodsPage.checkCardInfo(showing: "To update your personal card, please visit the Premier Inn Business website. To update your business card, please contact your Travel Manager.")
        paymentMethodsPage.goBack()

        MyAccountPage.goToHome()
    }

    func testPaymentMethodsUpdateCardMessageManager() {

        var mockPack = MockPack()
        mockPack.userMock = .managerBB

        load(mockPack: mockPack)

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.tapPaymentMethods()

        paymentMethodsPage.checkCardInfo(showing: "To update your cards, please visit the Premier Inn Business website.")
        paymentMethodsPage.goBack()

        MyAccountPage.goToHome()
    }

    // Self Booker

    func test_SelfBooker_CanBookPrepaymentCSCNotBAC() {

        var mockPack = MockPack()
        mockPack.userMock = .selfWhitbreadBB
        mockPack.company = .company2891NoBACInCSCAllowPersonal
        mockPack.availability = .liverpoolBBPrepaymentRequired
        load(mockPack: mockPack)

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.goToHome()

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnBusinessFlexRate()
        UpsellsPage.tapContinue()
        businessCardQuestionsPage.tapContinue()
        reviewAndBookPage.scrollDown(amount: 250)
        reviewAndBookPage.checkPayingWithCardNumber(withLastFourDigits: "1111")

        reviewAndBookPage.changePaymentMethodButtonAcc.perform(grey_tap())
        paymentMethodsPage.checkDisabledCard(withCardName: "twat")
        paymentMethodsPage.checkCardInfo(showing: "Company cards are not eligible for this rate.")
        paymentMethodsPage.checkSelectableCard(withCardName: "VI")
        paymentMethodsPage.cancel()

        reviewAndBookPage.goBack()
        businessCardQuestionsPage.goBack()
        UpsellsPage.goBack()
        hotelDetailsPage.goBack()
        searchResultsPage.goBack()
    }

    func test_SelfBooker_CanBookPrepaymentCSCBAC() {

        var mockPack = MockPack()
        mockPack.userMock = .selfWhitbreadBB
        mockPack.availability = .liverpoolBBPrepaymentRequired
        load(mockPack: mockPack)

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performBusinessLogin(username: "selfwhitbread@mailinator.com", password: "Hello123")
        MyAccountPage.goToHome()

        givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields()

        hotelDetailsPage.tapOnBusinessFlexRate()
        UpsellsPage.tapContinue()
        businessCardQuestionsPage.tapContinue()
        reviewAndBookPage.scrollDown(amount: 250)
        reviewAndBookPage.checkPayingWithCardNumber(withLastFourDigits: "1111")

        reviewAndBookPage.changePaymentMethodButtonAcc.perform(grey_tap())
        paymentMethodsPage.checkSelectableCard(withCardName: "bac")
        paymentMethodsPage.checkCardInfo(showing: "To update your card, please visit the Premier Inn Business website.")
        paymentMethodsPage.checkSelectableCard(withCardName: "VI")
        paymentMethodsPage.cancel()

        reviewAndBookPage.goBack()
        businessCardQuestionsPage.goBack()
        UpsellsPage.goBack()
        hotelDetailsPage.goBack()
        searchResultsPage.goBack()
    }
}
