//
//  BookingEarlGreyTests.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 26/05/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey
import Hippolyte

private struct MockPack {
    var login: LoginMock = .nickJones
    var userMock: GetUserMock = .nickJones
    var company: GetCompanyMock = .company2891
    var suggestions: SuggestionMock = .liverpool
    var places: PlacesLookupMock = .liverpool
    var availabilities: AvailabilitiesMock = .liverpool
    var availability: AvailabilityMock = .liverpool
    var hotel: HotelInfoMock = .liverpool
    var reviews: HotelReviewsMock = .liverpool
    var hold: HoldBookingMock = .success
    var validation: CardValidationMock = .visaCredit
    var booking: BookingMock = .payOnArrival
    var payment: PaymentMock = .successNo3DS

    static var hubPack: MockPack {
        var mockPack = MockPack()
        mockPack.suggestions = .hubKingsCross
        mockPack.places = .hubKingsCross
        mockPack.availability = .hubKingsCross
        mockPack.hotel = .hubKingsCross

        return mockPack
    }
}

class BookingEarlGreyTests: BaseEarlGreyTests {

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
        loadMock(with: mockPack.suggestions)
        loadMock(with: mockPack.places)
        loadMock(with: mockPack.availabilities)
        loadMock(with: mockPack.availability)
        loadMock(with: mockPack.hotel)
        loadMock(with: mockPack.reviews)
        loadMock(with: mockPack.hold)
        loadMock(with: mockPack.validation)
        loadMock(with: mockPack.booking)
        loadMock(with: mockPack.payment)
    }

    private func setupPayLaterStubs(with cardMock: CardValidationMock = .visaCredit) {

        var mockPack = MockPack()
        mockPack.validation = cardMock

        load(mockPack: mockPack)
    }

    private func setupPayNowStubs(using paymentMock: PaymentMock, with cardMock: CardValidationMock = .visaCredit) {

        var mockPack = MockPack()
        mockPack.validation = cardMock
        mockPack.booking = .payNow
        mockPack.payment = paymentMock

        load(mockPack: mockPack)
    }

    // MARK: Tests


    func testBookingPayOnArrival() {

        setupPayLaterStubs()

        givenIHaveSelectedAFlexRateAndPremierInnBreakfastAtLiverpoolMoorfield()

        reviewAndBookPage.check(rate: "Flex", extras: "Premier Inn Breakfast", total: "£82.95")
        reviewAndBookPage.selectPayment(interval: .later)
        reviewAndBookPage.acceptTerms()
        reviewAndBookPage.tapContinue()

        // verify booking was made
        bookingConfirmationPage.checkBookingIsSuccessful(for: .later, with: "Flex")
        bookingConfirmationPage.goBack()
        homePage.goToSearch()
    }


    func testBookingPayNowVisaCredit() {

        setupPayNowStubs(using: PaymentMock.successNo3DS)

        givenIHaveSelectedAFlexRateAndPremierInnBreakfastAtLiverpoolMoorfield()

        // check review and book ui for selected options
        reviewAndBookPage.check(rate: "Flex", extras: "Premier Inn Breakfast", total: "£82.95")

        // change to pay now
        reviewAndBookPage.selectPayment(interval: .now)
        reviewAndBookPage.scrollDown(amount: 100)
        reviewAndBookPage.enterCVV("123")
        reviewAndBookPage.acceptTerms()
        reviewAndBookPage.tapContinue()

        // verify booking was made
        bookingConfirmationPage.checkBookingIsSuccessful(for: .now, with: "Flex")
        bookingConfirmationPage.goBack()
        homePage.goToSearch()
    }

    func testBookingPayNowVisaDebit() {

        setupPayNowStubs(using: PaymentMock.successNo3DS, with: CardValidationMock.visaDebit)

        givenIHaveSelectedAFlexRateAndPremierInnBreakfastAtLiverpoolMoorfield(using: "4582620000000037")

        // check review and book ui for selected options
        reviewAndBookPage.check(rate: "Flex", extras: "Premier Inn Breakfast", total: "£82.95")

        // change to pay now
        reviewAndBookPage.selectPayment(interval: .now)
        reviewAndBookPage.scrollDown(amount: 100)
        reviewAndBookPage.enterCVV("123")
        reviewAndBookPage.acceptTerms()
        reviewAndBookPage.tapContinue()

        // verify booking was made
        bookingConfirmationPage.checkBookingIsSuccessful(for: .now, with: "Flex")
        bookingConfirmationPage.goBack()
        homePage.goToSearch()
    }

    func testBookingPayNowWith3DS() {

        setupPayNowStubs(using: PaymentMock.success3DS)

        givenIHaveSelectedAFlexRateAndPremierInnBreakfastAtLiverpoolMoorfield()

        // check review and book ui for selected options
        reviewAndBookPage.check(rate: "Flex", extras: "Premier Inn Breakfast", total: "£82.95")

        // change to pay now
        reviewAndBookPage.selectPayment(interval: .now)
        reviewAndBookPage.scrollDown(amount: 100)
        reviewAndBookPage.enterCVV("123")
        reviewAndBookPage.acceptTerms()
        reviewAndBookPage.tapContinue()

        // verify booking was made
        bookingConfirmationPage.checkBookingIsSuccessful(for: .now, with: "Flex")
        bookingConfirmationPage.goBack()
        homePage.goToSearch()
    }

    func testBookingFail() {

        setupPayNowStubs(using: PaymentMock.fail)

        givenIHaveSelectedAFlexRateAndPremierInnBreakfastAtLiverpoolMoorfield()

        // check review and book ui for selected options
        reviewAndBookPage.check(rate: "Flex", extras: "Premier Inn Breakfast", total: "£82.95")

        // change to pay now
        reviewAndBookPage.selectPayment(interval: .now)
        reviewAndBookPage.scrollDown(amount: 100)
        reviewAndBookPage.enterCVV("123")
        reviewAndBookPage.tapContinue()

        // check error visible
        reviewAndBookPage.checkErrorShowing()
        reviewAndBookPage.dismissError()

        // go back, many times
        reviewAndBookPage.goBack()
        cardDetailsPage.goBack()
        personalDetailsPage.goBack()
        UpsellsPage.goBack()
        hotelDetailsPage.goBack()
        searchResultsPage.goBack()
    }

    func testHubSemiFlexVisaDebitPayNowBooking() {

        var mockPack = MockPack.hubPack
        mockPack.validation = .visaDebit
        mockPack.booking = .payNow
        mockPack.payment = .successNo3DS

        load(mockPack: mockPack)

        givenIHaveSelectedASemiFlexRateAtHubKX(using: "4582620000000037")

        // check review and book ui for selected options
        let termsMessage = "Please note this booking can be cancelled up to 3 days before arrival. If you decide to cancel after that, you will be charged for the full value of the stay."

        reviewAndBookPage.check(rate: "Semi-Flex", termsMessage: termsMessage, extras: nil, total: "£102.00")
        reviewAndBookPage.scrollDown(amount: 200)
        reviewAndBookPage.enterCVV("123")
        reviewAndBookPage.acceptTerms()
        reviewAndBookPage.tapContinue()

        // reset journey
        bookingConfirmationPage.checkBookingIsSuccessful(for: .now, with: "Semi-Flex")
        bookingConfirmationPage.goBack()

        homePage.goToSearch()
    }

    func testFlexLoggedInForSomeoneElseWithWifiBooking() {

        load(mockPack: MockPack())

        givenIhaveLoggedInSelectedAFlexRateWifiAndBookingForSomeoneElse()

        let termsMessage = "If you need to cancel or change your reservation, you can do so before 1:00 pm on your arrival date. If you cancel after 1:00 pm you will be charged for a night's accommodation for each room you have booked. Cancellation references should be kept as proof of cancellation."

        reviewAndBookPage.check(rate: "Flex", termsMessage: termsMessage, extras: "Upgrade to Ultimate Wi-Fi ", total: "£78.00")
        reviewAndBookPage.check(booker: "Mr Nick Twisp")
        reviewAndBookPage.check(roomGuest: "Mr Testes Testes")
        reviewAndBookPage.selectPayment(interval: .later)
        reviewAndBookPage.acceptTerms()
        reviewAndBookPage.tapContinue()

        // verify booking was made
        bookingConfirmationPage.checkBookingIsSuccessful(for: .later, with: "Flex")
        bookingConfirmationPage.goBack()
        homePage.goToSearch()
    }

    func testFlexLoggedInBACStoredCard() {

        var mockPack = MockPack()
        mockPack.login = .dickDollars
        mockPack.userMock = .userWithStoredBAC

        load(mockPack: mockPack)

        givenIHaveLoggedInToBACAccountSelectedFlex()

        let termsMessage = "If you need to cancel or change your reservation, you can do so before 1:00 pm on your arrival date. If you cancel after 1:00 pm you will be charged for a night's accommodation for each room you have booked. Cancellation references should be kept as proof of cancellation."

        reviewAndBookPage.check(rate: "Flex", termsMessage: termsMessage, extras: nil, total: "£73.00")
        reviewAndBookPage.check(booker: "Mr Dick Dollars")
        reviewAndBookPage.checkPayingWithCardNumber(withLastFourDigits: "5918")
        reviewAndBookPage.acceptTerms()
        reviewAndBookPage.tapContinue()

        bookingConfirmationPage.checkBookingIsSuccessful(for: .later, with: "Flex")
        bookingConfirmationPage.goBack()
        homePage.goToSearch()
    }

    func testBookingPayNowWithTimeoutAndPriceIncrease() {

        var mockPack = MockPack()
        mockPack.validation = .visaCredit

        load(mockPack: mockPack)

        givenIHaveSelectedAFlexRateAndPremierInnBreakfastAtLiverpoolMoorfield()

        Hippolyte.shared.stop()

        mockPack.availability = .liverpoolExpensive
        mockPack.booking = .payNow
        mockPack.payment = .timeout

        load(mockPack: mockPack)

        Hippolyte.shared.start()

        reviewAndBookPage.check(rate: "Flex", extras: "Premier Inn Breakfast", total: "£82.95")
        reviewAndBookPage.selectPayment(interval: .now)
        reviewAndBookPage.scrollDown(amount: 100)
        reviewAndBookPage.enterCVV("123")
        reviewAndBookPage.acceptTerms()
        reviewAndBookPage.tapContinue()

        //timeout shenanigans
        reviewAndBookPage.checkTimeoutNewRateShowing()

        Hippolyte.shared.stop()

        mockPack.booking = .payNow
        mockPack.payment = .successNo3DS

        load(mockPack: mockPack)

        Hippolyte.shared.start()

        reviewAndBookPage.acceptNewRate()
        reviewAndBookPage.enterCVV("123")
        reviewAndBookPage.tapContinue()

        bookingConfirmationPage.checkBookingIsSuccessful(for: .now, with: "Flex")
    }
}
