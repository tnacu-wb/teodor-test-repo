//
//  CheckInOnlineEarlGreyTests.swift
//  PremierInnUITests
//
//  Created by Georgios Aikaterinakis on 01/09/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey
import Hippolyte
import SimpleNetwork

private struct MockPack {
    var login: LoginMock = .nickJones
    var userMock: GetUserMock = .nickJones
    var reservations: ReservationsMock = .nickJones
    var reservation: ReservationMock = .jonesVictoria
    var hotel: HotelInfoMock = .victoria
    var cleanCIOLSession: CleanCIOLSessionMock = .cleanSession
    var startCheckIn: StartCIOLMock = .paymentAllowedPaymentRequired
    var ciolGuests: CIOLGuestsMock = .guests
    var ciolUpsells: CIOLUpsellsMock = .upsells
    var ciolPayment: CIOLPaymentMock = .successNo3DS
    var cardValidation: CardValidationMock = .visaCredit
}

class CheckInOnlineEarlGreyTests: BaseEarlGreyTests {

    // MARK: Boilerplate

    override func setUp() {
        super.setUp()

        setupMockAPI()

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
    }

    override func tearDown() {

        MyAccountPage.goToHome()
        
        super.tearDown()
    }

    // MARK: Config

    override func setupMockAPI() {

        Hippolyte.shared.start()
    }

    private func setupCheckInOnlineStub(using mockConfig: MockConfig) {

        Hippolyte.shared.clearStubs()

        loadMock(with: LoginMock.nickJones)
        loadMock(with: GetUserMock.nickJones)
        loadMock(with: mockConfig)
    }

    // MARK: Tests

    func testCheckInOnlineDisabled() {

        setupCheckInOnlineStub(using: ReservationsMock.checkInOnlineDisabled)

        givenIHaveLoggedInAndViewMyBookings()

        EarlGrey.selectElement(with: grey_accessibilityLabel("Check in online")).assert(grey_notVisible())
    }

    func testCheckInOnlineEnabled() {

        setupCheckInOnlineStub(using: ReservationsMock.checkInOnlineEnabled)

        givenIHaveLoggedInAndViewMyBookings()

        EarlGrey.selectElement(with: grey_accessibilityLabel("Check in online")).assert(grey_sufficientlyVisible())
    }

    func testCheckInOnlineEnabledButCheckedIn() {

        setupCheckInOnlineStub(using: ReservationsMock.checkInOnlineEnabledButCheckedIn)

        givenIHaveLoggedInAndViewMyBookings()

        EarlGrey.selectElement(with: grey_accessibilityLabel("Check in online")).assert(grey_notVisible())
    }
}

// Added a new test class so I don't have to comment out so many tests 😅
class CheckInOnlineGuestDetailsEarlGreyTests: BaseEarlGreyTests {

    // MARK: Boilerplate

    override func setUp() {
        super.setUp()

        removeLocalReservations()

        setupMockAPI()

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
    }

    override func tearDown() {

        MyAccountPage.goToHome()

        super.tearDown()
    }

    // MARK: Config

    private func removeLocalReservations() {

        let storageManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)
        let stays: [Stay] = storageManager.items
        for stay in stays { _ = storageManager.remove(stay) }
    }

    private func load(mockPack: MockPack) {

        Hippolyte.shared.clearStubs()

        loadMock(with: mockPack.login)
        loadMock(with: mockPack.userMock)
        loadMock(with: mockPack.reservations)
        loadMock(with: mockPack.reservation)
        loadMock(with: mockPack.hotel)
        loadMock(with: mockPack.startCheckIn)
        loadMock(with: mockPack.cleanCIOLSession)
        loadMock(with: mockPack.ciolGuests)
        loadMock(with: mockPack.ciolUpsells)
        loadMock(with: mockPack.ciolPayment)
        loadMock(with: mockPack.cardValidation)
    }

    // MARK: Tests

    func testCheckInOnlineVictoriaPaymentRequired() {

        var mockPack = MockPack()
        mockPack.reservations = .nickJones
        mockPack.reservation = .jonesVictoria
        mockPack.hotel = .victoria
        mockPack.startCheckIn = .paymentAllowedPaymentRequired
        mockPack.ciolGuests = .guests
        mockPack.ciolUpsells = .upsellsPaymentRequired
        mockPack.ciolPayment = .successNo3DS

        load(mockPack: mockPack)

        givenIHaveLoggedInAndViewMyBookings()

        EarlGrey.selectElement(with: grey_accessibilityLabel("Check in online")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_accessibilityLabel("Check in online")).perform(grey_tap())

        checkInOnlinePage.checkPaymentRequired()
        checkInOnlinePage.tapContinueToPayment()

        paymentDetailsPage.checkPaymentAllowed()
        paymentDetailsPage.goBack()
        checkInOnlinePage.goBack()
    }

    func testCheckInOnlineVictoriaPaymentNotRequired() {

        var mockPack = MockPack()
        mockPack.reservations = .nickJones
        mockPack.reservation = .jonesVictoria
        mockPack.hotel = .victoria
        mockPack.startCheckIn = .paymentAllowedPaymentNotRequired

        load(mockPack: mockPack)

        givenIHaveLoggedInAndViewMyBookings()

        EarlGrey.selectElement(with: grey_accessibilityLabel("Check in online")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_accessibilityLabel("Check in online")).perform(grey_tap())

        checkInOnlinePage.checkCheckInPossible()
        checkInOnlinePage.goBack()
    }

    func testCIOLPaymentWithChangeCard() {

        var mockPack = MockPack()
        mockPack.reservations = .nickJones
        mockPack.reservation = .jonesVictoria
        mockPack.hotel = .victoria
        mockPack.startCheckIn = .paymentAllowedPaymentRequired
        mockPack.ciolGuests = .guests
        mockPack.ciolUpsells = .upsellsPaymentRequired
        mockPack.cardValidation = .visaCredit

        load(mockPack: mockPack)

        givenIHaveLoggedInAndViewMyBookings()

        EarlGrey.selectElement(with: grey_accessibilityLabel("Check in online")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_accessibilityLabel("Check in online")).perform(grey_tap())

        checkInOnlinePage.checkPaymentRequired()
        checkInOnlinePage.tapContinueToPayment()

        paymentDetailsPage.tapChangeCard()

        cardDetailsPage.enterMakePaymentTestData(for: "4444333322221111")
        cardDetailsPage.tapPayNowAndCheckIn()
    }
}

class CheckInOnlineSummaryEarlGreyTests: BaseEarlGreyTests {

    // MARK: Boilerplate

    override func setUp() {
        super.setUp()

        setupMockAPI()

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
    }

    override func tearDown() {

        MyAccountPage.goToHome()

        super.tearDown()
    }

    // MARK: Config

    private func load(mockPack: MockPack) {

        Hippolyte.shared.clearStubs()

        loadMock(with: mockPack.login)
        loadMock(with: mockPack.userMock)
        loadMock(with: mockPack.reservations)
        loadMock(with: mockPack.reservation)
        loadMock(with: mockPack.hotel)
        loadMock(with: mockPack.startCheckIn)
        loadMock(with: mockPack.cleanCIOLSession)
        loadMock(with: mockPack.ciolGuests)
        loadMock(with: mockPack.ciolUpsells)
    }

    // MARK: Tests

    func testCheckInOnlineVictoriaPaymentNotRequired() {

        var mockPack = MockPack()
        mockPack.reservations = .staysCiolBooking
        mockPack.reservation = .ciolBooking
        mockPack.hotel = .victoria
        mockPack.startCheckIn = .paymentAllowedPaymentNotRequired

        load(mockPack: mockPack)

        givenIHaveLoggedInAndViewMyBookings()

        EarlGrey.selectElement(with: grey_accessibilityLabel("Check in online")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Check in online"), grey_interactable()])).perform(grey_tap())

        CheckInOnlinePage().checkCheckInPossible()
        CheckInOnlinePage().tapCheckIn()

        CheckInOnlinePage().checkCheckedInIsShown()
    }
}

class CheckInOnlineCancelableTextEarlGreyTests: BaseEarlGreyTests {

    // MARK: Boilerplate

    override func setUp() {
        super.setUp()

        setupMockAPI()

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
    }

    override func tearDown() {

        MyAccountPage.goToHome()

        super.tearDown()
    }

    // MARK: Config

    private func load(mockPack: MockPack) {

        Hippolyte.shared.clearStubs()

        loadMock(with: mockPack.login)
        loadMock(with: mockPack.userMock)
        loadMock(with: mockPack.reservations)
        loadMock(with: mockPack.reservation)
        loadMock(with: mockPack.hotel)
        loadMock(with: mockPack.startCheckIn)
        loadMock(with: mockPack.cleanCIOLSession)
        loadMock(with: mockPack.ciolGuests)
        loadMock(with: mockPack.ciolUpsells)
    }

    // MARK: Tests

    func testCheckInOnlinePaymentDetails() {

        var mockPack = MockPack()
        mockPack.reservations = .staysCiolBooking
        mockPack.reservation = .ciolBooking
        mockPack.hotel = .victoria
        mockPack.startCheckIn = .paymentAllowedPaymentRequired
        mockPack.ciolUpsells = .upsellsPaymentRequired

        load(mockPack: mockPack)

        givenIHaveLoggedInAndViewMyBookings()

        EarlGrey.selectElement(with: grey_accessibilityLabel("Check in online")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Check in online"), grey_interactable()])).perform(grey_tap())

        CheckInOnlinePage().checkPaymentRequired()
        CheckInOnlinePage().tapContinueToPayment()

        EarlGrey.selectElement(with: grey_accessibilityLabel("Please note this reservation cannot be cancelled without incurring a charge. Please call Alpha2 London Victoria Premier Inn, 0333 321 1276, in order to proceed your request any further. Calls are charged at the national rate")).assert(grey_sufficientlyVisible())

        PaymentDetailsPage().goBack()
        CheckInOnlinePage().goBack()
    }

    func testCheckInOnlineCardDetails() {

        var mockPack = MockPack()
        mockPack.reservations = .staysCiolBooking
        mockPack.reservation = .ciolBooking
        mockPack.hotel = .victoria
        mockPack.startCheckIn = .paymentAllowedPaymentRequired
        mockPack.ciolUpsells = .upsellsPaymentRequired

        load(mockPack: mockPack)

        givenIHaveLoggedInAndViewMyBookings()

        EarlGrey.selectElement(with: grey_accessibilityLabel("Check in online")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Check in online"), grey_interactable()])).perform(grey_tap())

        CheckInOnlinePage().checkPaymentRequired()
        CheckInOnlinePage().tapContinueToPayment()
        PaymentDetailsPage().tapChangeCard()

        cardDetailsPage.checkCancelableText(with: "Please note this reservation cannot be cancelled without incurring a charge. Please call Alpha2 London Victoria Premier Inn, 0333 321 1276, in order to proceed your request any further. Calls are charged at the national rate")
        
        cardDetailsPage.goBack()
        PaymentDetailsPage().goBack()
        CheckInOnlinePage().goBack()
    }
}
