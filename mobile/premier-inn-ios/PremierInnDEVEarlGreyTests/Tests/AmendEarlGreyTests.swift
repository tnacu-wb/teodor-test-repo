//
//  AmendEarlGreyTests.swift
//  PremierInnUITests
//
//  Created by Freddie Parks on 23/09/2020.
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
    var availability: AvailabilityMock = .standardRooms
}

class AmendBaseTests: BaseEarlGreyTests {

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

    override func setupMockAPI() {

        Hippolyte.shared.start()
    }

    fileprivate func load(mockPack: MockPack) {

        Hippolyte.shared.clearStubs()

        loadMock(with: mockPack.login)
        loadMock(with: mockPack.userMock)
        loadMock(with: mockPack.reservations)
        loadMock(with: mockPack.reservation)
        loadMock(with: mockPack.hotel)
        loadMock(with: mockPack.availability)
    }

    private func removeLocalReservations() {

        let storageManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)
        let stays: [Stay] = storageManager.items
        for stay in stays { _ = storageManager.remove(stay) }
    }
}

class AmendEarlGreyTests: AmendBaseTests {

    // MARK: - Not Amendable Tests

    func testNotAmendable() {

        var mockPack = MockPack()
        mockPack.reservations = .nickJonesOnlyVictoria
        mockPack.reservation = .jonesVictoriaNotAmendable

        load(mockPack: mockPack)

        givenIHaveLoggedInAndViewMyBookings()

        MyBookingsPage().selectBookingDetails()

        bookingConfirmationPage.selectManangeBooking()

        AmendBookingPage.checkCannotAmendMealsAndExtras()
        AmendBookingPage.checkCannotAmendAndCancel()

        AmendBookingPage.goBack()

        bookingConfirmationPage.goBackBookingsDetails()
    }

    // MARK: - Amend Restricted Tests

    func testChangeDatesAmendRestricted() {

        var mockPack = MockPack()
        mockPack.reservations = .nickJonesOnlyVictoria
        mockPack.reservation = .jonesVictoriaAmendRestricted

        load(mockPack: mockPack)

        givenIHaveLoggedInAndViewMyBookings()

        MyBookingsPage().selectBookingDetails()

        bookingConfirmationPage.selectManangeBooking()

        AmendBookingPage.checkCannotAmendMealsAndExtras()
        AmendBookingPage.checkCannotReviewAmends()
        checkScrolling("amendTableView", forLabel: "Dates")
        checkScrolling("amendTableView", forLabel: "Mon, Nov 10 - Wed, Nov 12 (2 nights)")

        // Amend Dates
        AmendBookingPage.tapAmendDates()

        // this may be a bit hit or miss depending on the date
        EarlGrey.selectElement(with: grey_accessibilityLabel("1")).atIndex(0).perform(grey_tap())
        EarlGrey.selectElement(with: grey_accessibilityLabel("4")).atIndex(0).perform(grey_tap())

        CalendarPage.checkCanOnlyChangeTheCheckInDate()

        CalendarPage.goBack()

        AmendBookingPage.goBack()

        bookingConfirmationPage.goBackBookingsDetails()
    }

    func testAmendUpsellsRestricted() {

        var mockPack = MockPack()
        mockPack.reservations = .nickJonesOnlyVictoria
        mockPack.reservation = .jonesVictoriaAmendRestricted

        load(mockPack: mockPack)

        givenIHaveLoggedInAndViewMyBookings()

        MyBookingsPage().selectBookingDetails()

        bookingConfirmationPage.selectManangeBooking()

        // Checks
        AmendBookingPage.checkCannotAmendMealsAndExtras()
        AmendBookingPage.checkCannotReviewAmends()
        checkScrolling("amendTableView", forLabel: "Dates")
        checkScrolling("amendTableView", forLabel: "Mon, Nov 10 - Wed, Nov 12 (2 nights)")

        // Room 1
        checkScrolling("amendTableView", forLabel: "Room 1")
        checkScrolling("amendTableView", forLabel: "Meals")
        checkScrolling("amendTableView", forLabel: "Meal Deal (Breakfast & Dinner) x 1")
        checkScrolling("amendTableView", forLabel: "Extras")
        checkScrolling("amendTableView", forLabel: "Ultimate Wi-Fi - 24 hours")

        // Amend Room 1
        AmendBookingPage.tapEditGuestsForRoom1()

        EditRoomPage.checkCannotChangeLeadGuest()
        EditRoomPage.goBack()

        AmendBookingPage.goBack()

        bookingConfirmationPage.goBackBookingsDetails()
    }

    // MARK: - Amend Not Restricted Tests

    func testChangeDates() {

        var mockPack = MockPack()
        mockPack.reservations = .nickJonesOnlyVictoria
        mockPack.reservation = .jonesVictoriaAmendNotRestricted

        load(mockPack: mockPack)

        givenIHaveLoggedInAndViewMyBookings()

        MyBookingsPage().selectBookingDetails()

        bookingConfirmationPage.selectManangeBooking()

        AmendBookingPage.checkCanAmendMealsAndExtras()
        AmendBookingPage.checkCannotReviewAmends()
        checkScrolling("amendTableView", forLabel: "Dates")
        checkScrolling("amendTableView", forLabel: "Mon, Nov 10 - Wed, Nov 12 (2 nights)")

        // Amend Dates
        AmendBookingPage.tapAmendDates()

        // this may be a bit hit or miss depending on the date
        EarlGrey.selectElement(with: grey_accessibilityLabel("1")).atIndex(0).perform(grey_tap())
        EarlGrey.selectElement(with: grey_accessibilityLabel("4")).atIndex(0).perform(grey_tap())

        CalendarPage.tapCheckAvailability()
        EarlGrey.selectElement(with: grey_accessibilityLabel("Continue (3 nights)")).perform(grey_tap())

        // if there are no restrictions it shows Upsell page
        UpsellsPage.tapContinue()

        checkScrolling("amendTableView", forLabel: "Dates")
        checkScrolling("amendTableView", forLabel: "Sun, Nov 1 - Wed, Nov 4 (3 nights)")
        AmendBookingPage.checkCanReviewAmends()

        AmendBookingPage.goBack()

        bookingConfirmationPage.goBackBookingsDetails()
    }

    func testAmendUpsells() {

        var mockPack = MockPack()
        mockPack.reservations = .nickJonesOnlyVictoria
        mockPack.reservation = .jonesVictoriaAmendNotRestricted

        load(mockPack: mockPack)

        givenIHaveLoggedInAndViewMyBookings()

        MyBookingsPage().selectBookingDetails()

        bookingConfirmationPage.selectManangeBooking()

        AmendBookingPage.checkCanEditDates()
        AmendBookingPage.checkCanAmendMealsAndExtras()
        AmendBookingPage.checkCannotReviewAmends()

        // Check everything is shown
        EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Meals"), grey_interactable()])).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Extras"), grey_interactable()])).assert(grey_sufficientlyVisible())

        // Amend Meals and Extras
        AmendBookingPage.tapAmendMealsAndExtras()

        UpsellsPage.addMeal(.NoMeals)
        UpsellsPage.addExtra(.FreeWiFi)
        UpsellsPage.tapContinue()

        // Check everything is shown
        EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Meals"), grey_interactable()])).assert(grey_notVisible())
        EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Extras"), grey_interactable()])).assert(grey_notVisible())

        AmendBookingPage.goBack()

        bookingConfirmationPage.goBackBookingsDetails()
    }

    func testAddRoom() {

        var mockPack = MockPack()
        mockPack.reservations = .nickJonesOnlyVictoria
        mockPack.reservation = .jonesVictoriaAmendNotRestricted

        load(mockPack: mockPack)

        givenIHaveLoggedInAndViewMyBookings()

        MyBookingsPage().selectBookingDetails()

        bookingConfirmationPage.selectManangeBooking()

        AmendBookingPage.checkCannotReviewAmends()

        // Add Room
        AmendBookingPage.tapAddRoom()

        AmendAddRoomPage.checkAvailabilityButtonIsShown()
        AmendAddRoomPage.tapCheckAvailability()

        AmendAddRoomPage.enterTestGuestDetails()
        AmendAddRoomPage.tapContinue()

        UpsellsPage.addMeal(.ContinentalBreakfast)
        UpsellsPage.addExtra(.UltimateWiFi)
        UpsellsPage.tapContinue()

        // Check everything is shown
        AmendBookingPage.checkCanReviewAmends()

        // Room 1
        checkScrolling("amendTableView", forLabel: "Room 1")
        checkScrolling("amendTableView", forLabel: "Meals")
        checkScrolling("amendTableView", forLabel: "Meal Deal (Breakfast & Dinner) x 1")
        checkScrolling("amendTableView", forLabel: "Extras")
        checkScrolling("amendTableView", forLabel: "Ultimate Wi-Fi - 24 hours")

        // Room 2
        checkScrolling("amendTableView", forLabel: "Room 2")
        checkScrolling("amendTableView", forLabel: "Meals")
        checkScrolling("amendTableView", forLabel: "Continental Breakfast x 1")
        checkScrolling("amendTableView", forLabel: "Extras")
        checkScrolling("amendTableView", forLabel: "Ultimate Wi-Fi - 24 hours")

        AmendBookingPage.tapReviewAmends()

        // Check Review Amends
        checkScrolling("amendTableView", forLabel: "Continental Breakfast")

        ReviewAndBookPage().goBack()
        AmendBookingPage.goBack()

        bookingConfirmationPage.goBackBookingsDetails()
    }
}

class AmendCardAuthenticationEarlGreyTests: AmendBaseTests {

    // MARK: - Amend Not Restricted - Card Authentication Test

    func testCardAuthenticationRequired() {

        var mockPack = MockPack()
        mockPack.reservations = .staysWithCardAuthenticationRequired
        mockPack.reservation = .reservationsWithCardAuthenticationRequired
        mockPack.hotel = .hotelsWithCardAuthenticationRequired

        load(mockPack: mockPack)

        givenIHaveLoggedInAndViewMyBookings()

        MyBookingsPage().selectBookingDetails()

        bookingConfirmationPage.selectManangeBooking()

        AmendBookingPage.checkCannotReviewAmends()

        // Add Room
        AmendBookingPage.tapAddRoom()

        AmendAddRoomPage.checkAvailabilityButtonIsShown()
        AmendAddRoomPage.tapCheckAvailability()

        AmendAddRoomPage.enterTestGuestDetails()
        AmendAddRoomPage.tapContinue()

        UpsellsPage.addMeal(.ContinentalBreakfast)
        UpsellsPage.addExtra(.UltimateWiFi)
        UpsellsPage.tapContinue()

        // Check everything is shown
        AmendBookingPage.checkCanReviewAmends()

        // Room 1
        checkScrolling("amendTableView", forLabel: "Room 1")
        checkScrolling("amendTableView", forLabel: "Meals")
        checkScrolling("amendTableView", forLabel: "Meal Deal (Breakfast & Dinner) x 1")
        checkScrolling("amendTableView", forLabel: "Extras")
        checkScrolling("amendTableView", forLabel: "Ultimate Wi-Fi - 24 hours")

        // Room 2
        checkScrolling("amendTableView", forLabel: "Room 2")
        checkScrolling("amendTableView", forLabel: "Meals")
        checkScrolling("amendTableView", forLabel: "Continental Breakfast x 1")
        checkScrolling("amendTableView", forLabel: "Extras")
        checkScrolling("amendTableView", forLabel: "Ultimate Wi-Fi - 24 hours")

        AmendBookingPage.tapReviewAmends()

        // Check Review Amends
        checkScrolling("amendReviewTableView", forLabel: "Continental Breakfast")
        checkScrolling("amendReviewTableView", forElement: ReviewAmendmentsPage.paymentMethodLabel)
        checkScrolling("amendReviewTableView", forLabel: "VI")
        checkScrolling("amendReviewTableView", forLabel: "Ending in 1111")
        checkScrolling("amendReviewTableView", forElement: ReviewAmendmentsPage.confirmChangesButton)
        checkScrolling("amendReviewTableView", forElement: ReviewAmendmentsPage.paymentAuthenticationLabel)

        ReviewAndBookPage().goBack()
        AmendBookingPage.goBack()

        bookingConfirmationPage.goBackBookingsDetails()
    }

    func testCardAuthenticationRequiredWithBusinessAccountCard() {

        var mockPack = MockPack()
        mockPack.reservations = .staysWithCardAuthenticationRequired
        mockPack.reservation = .reservationsWithCardAuthenticationRequiredWithBAC
        mockPack.hotel = .hotelsWithCardAuthenticationRequired

        load(mockPack: mockPack)

        givenIHaveLoggedInAndViewMyBookings()

        MyBookingsPage().selectBookingDetails()

        bookingConfirmationPage.selectManangeBooking()

        AmendBookingPage.checkCannotReviewAmends()

        // Add Room
        AmendBookingPage.tapAddRoom()

        AmendAddRoomPage.checkAvailabilityButtonIsShown()
        AmendAddRoomPage.tapCheckAvailability()

        AmendAddRoomPage.enterTestGuestDetails()
        AmendAddRoomPage.tapContinue()

        UpsellsPage.addMeal(.ContinentalBreakfast)
        UpsellsPage.addExtra(.UltimateWiFi)
        UpsellsPage.tapContinue()

        // Check everything is shown
        AmendBookingPage.checkCanReviewAmends()

        // Room 1
        checkScrolling("amendTableView", forLabel: "Room 1")
        checkScrolling("amendTableView", forLabel: "Meals")
        checkScrolling("amendTableView", forLabel: "Meal Deal (Breakfast & Dinner) x 1")
        checkScrolling("amendTableView", forLabel: "Extras")
        checkScrolling("amendTableView", forLabel: "Ultimate Wi-Fi - 24 hours")

        // Room 2
        checkScrolling("amendTableView", forLabel: "Room 2")
        checkScrolling("amendTableView", forLabel: "Meals")
        checkScrolling("amendTableView", forLabel: "Continental Breakfast x 1")
        checkScrolling("amendTableView", forLabel: "Extras")
        checkScrolling("amendTableView", forLabel: "Ultimate Wi-Fi - 24 hours")

        AmendBookingPage.tapReviewAmends()

        // Check Review Amends
        checkScrolling("amendReviewTableView", forLabel: "Continental Breakfast")
        ReviewAmendmentsPage.paymentMethodLabel.assert(grey_notVisible())
        ReviewAmendmentsPage.confirmChangesButton.assert(grey_sufficientlyVisible())
        ReviewAmendmentsPage.paymentAuthenticationLabel.assert(grey_notVisible())

        ReviewAndBookPage().goBack()
        AmendBookingPage.goBack()

        bookingConfirmationPage.goBackBookingsDetails()
    }
}
