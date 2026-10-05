//
//  BookingDetailsTests.swift
//  PremierInn
//
//  Created by Marcello Mascia on 12/12/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class BookingDetailsTests: XCTestCase {

    private var sut: BookingDetails!
    
    override func setUp() {
        super.setUp()

        sut = BookingDetails()
    }
    
    override func tearDown() {
        sut = nil

        super.tearDown()
    }

    static let reservationDictionary: PIDictionary = {
        let fileURL = Bundle.module.url(forResource: "reservation", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        return jsonDictionary
    }()

    private enum Constants {

        static let cost: Cost = Cost(amount: 12, currencyCode: "GBP")

        static let rateWithPromotionCode = Rate(dictionary: ["promotionCode": "FX10R"])
        static let rateWithEmptyPromotionCode = Rate(dictionary: ["promotionCode": ""])
        static let rateWithoutPromotionCode = Rate(dictionary: [:])
    }

	func testSetRate_WithoutUserPreferences() {

		sut.rate = Rate(
            uniqueID: UUID(),
            isBiggerRoom: false,
			code: "C",
			totalCost: Cost(amount: 12, currencyCode: "GBP"),
			description: nil,
            name: "Flex",
			text: nil,
            classification: "A",
			rooms: nil,
			upsellItems: nil,
            cellCode: .none,
            promotionCode: nil,
            lettingTypes: []
		)
		XCTAssertNil(sut.meal)
	}

	func testSetRate_WithUserMealPreferences() {

		let preferredMeal = try! UpsellItem(dictionary: ["operaId": "MDP", "legend": "Meal Deal", "foodUpsell": true, "price": ["amount": "10", "currency": "GBP"]])

		sut.booker = {
			let user = try! User(title: "Mr", firstName: "John", lastName: "Doe")
			user.bookingPreference?.foodPreference = MealOption.mealDeal

			return user
		}()

		sut.rate = Rate(
            uniqueID: UUID(),
            isBiggerRoom: false,
			code: "C",
			totalCost: Cost(amount: 12, currencyCode: "GBP"),
			description: nil,
            name: "Flex",
			text: nil,
            classification: "A",
			rooms: nil,
			upsellItems: [preferredMeal],
            cellCode: .none,
            promotionCode: nil,
            lettingTypes: []
		)

		XCTAssertEqual(sut.meal, preferredMeal)
	}

	func testRoomAndMealCost() {

		XCTAssertNil(sut.roomAndMealCost)

        sut.roomLettings = {
            let room = Room()

            room.options = {

                var roomOption = RoomLettingOption()
                roomOption.totalCost = Cost(amount: 12, currencyCode: "GBP")

                return [roomOption]
            }()

            return [room]
        }()

		XCTAssertEqual(sut.roomAndMealCost, Cost(amount: 12, currencyCode: "GBP"))

        let meal = try! UpsellItem(dictionary: ["operaId": "BFADBF", "legend": "Premier Inn Breakfast", "foodUpsell": true, "price": ["amount": "10", "currency": "GBP"]])
        sut.roomMealCombos = [RoomMealCombo(roomNumber: 0, meal: meal, quantity: 1)]

		XCTAssertEqual(sut.roomAndMealCost, Cost(amount: 22, currencyCode: "GBP"))
	}

	func testTotalCost() {

		XCTAssertEqual(sut.totalCost, .zeroPounds)

        sut.roomLettings = {
            let room = Room()

            room.options = {

                var roomOption = RoomLettingOption()
                roomOption.totalCost = Cost(amount: 12, currencyCode: "GBP")

                return [roomOption]
            }()

            return [room]
        }()

        sut.roomMealCombos = []
		XCTAssertEqual(sut.totalCost, Cost(amount: 12, currencyCode: "GBP"))

		let meal = try! UpsellItem(dictionary: ["operaId": "BFADBF", "legend": "Premier Inn Breakfast", "foodUpsell": true, "price": ["amount": "10", "currency": "GBP"]])
        sut.roomMealCombos = [RoomMealCombo(roomNumber: 0, meal: meal, quantity: 1)]

		XCTAssertEqual(sut.totalCost, Cost(amount: 22, currencyCode: "GBP"))
	}

    func testMealTotalCost() {

        let meal = try! UpsellItem(
            dictionary: ["operaId": "BFADBF",
                         "legend": "Premier Inn Breakfast",
                         "foodUpsell": true,
                         "price": ["amount": "10", "currency": "GBP"]]
        )

        // When 2 adults have meal
        sut.roomMealCombos = [RoomMealCombo(roomNumber: 0, meal: meal, quantity: 2)]
        XCTAssertEqual(sut.mealTotalCost, Cost(amount: 20, currencyCode: "GBP"))

        // When 1 adult has meal
        sut.roomMealCombos = [RoomMealCombo(roomNumber: 0, meal: meal, quantity: 1)]
        XCTAssertEqual(sut.mealTotalCost, Cost(amount: 10, currencyCode: "GBP"))
    }

	func testBookingDetailsPayment() {

		XCTAssertTrue(sut.totalCost.amount == Cost.zeroPounds.amount)

		sut.rate = Rate(dictionary: ["classification": "F"])
        sut.roomLettings = {
            let room = Room()

            room.options = {

                var roomOption = RoomLettingOption()
                roomOption.totalCost = Cost(amount: 191.50, currencyCode: "GBP")

                return [roomOption]
            }()

            return [room]
        }()

		XCTAssertEqual(sut.totalCost.amount, 191.50)

		let meal = try! UpsellItem(dictionary: ["operaId": "BFADBF", "legend": "Premier Inn Breakfast", "foodUpsell": true, "price": ["amount": "10", "currency": "GBP"]])
		XCTAssertNotNil(meal)

        sut.roomMealCombos = [RoomMealCombo(roomNumber: 0, meal: meal, quantity: 1)]
		XCTAssertEqual(sut.totalCost.amount, 201.50)
	}

    func testBookingDetailsWithReservation() {
        let reservation = try! Reservation(dictionary: BookingDetailsTests.reservationDictionary)
        let arrivalDate = Date()
        sut = BookingDetails(reservation: reservation)


        XCTAssertEqual(sut?.criteria.rooms.count, 1)
        XCTAssertEqual(sut?.criteria.nights, 1)
        XCTAssertEqual(sut?.criteria.arrivalDate.parameterString, arrivalDate.parameterString)
    }

    func testBookingDetailsNightsAndDays() {

        let arrivalDate = Date()
        let bookingDetails = BookingDetails(numberOfNights: 1, arrivalDate: arrivalDate)

        XCTAssertEqual(bookingDetails.criteria.nights, 1)
        XCTAssertEqual(bookingDetails.criteria.arrivalDate, arrivalDate)
    }

    // MARK: - isPromotionalBooking tests

    func testIsPromotionalBookingAllPromoCodesNilOrEmptyReturnsFalse() {
        // GIVEN
        sut.appIncentivePromoCode = nil
        sut.freeBreakfastPromoCode = nil
        sut.siteWidePromoCode = nil
        sut.userEnteredPromoCode = nil
        sut.rate = Constants.rateWithoutPromotionCode

        // WHEN
        let result = sut.isPromotionalBooking

        // THEN
        XCTAssertFalse(result, "Should be false when all promo codes are nil or empty")
    }

    func testIsPromotionalBookingAppIncentivePromoCodePresentReturnsTrue() {
        // GIVEN
        sut.appIncentivePromoCode = "PROMO"
        sut.freeBreakfastPromoCode = nil
        sut.siteWidePromoCode = nil
        sut.userEnteredPromoCode = nil
        sut.rate = Constants.rateWithoutPromotionCode

        // WHEN
        let result = sut.isPromotionalBooking

        // THEN
        XCTAssertTrue(result, "Should be true when app incentive promo code is present")
    }

    func testIsPromotionalBookingFreeBreakfastPromoCodePresentReturnsTrue() {
        // GIVEN
        sut.appIncentivePromoCode = nil
        sut.freeBreakfastPromoCode = "FB"
        sut.siteWidePromoCode = nil
        sut.userEnteredPromoCode = nil
        sut.rate = Constants.rateWithoutPromotionCode

        // WHEN
        let result = sut.isPromotionalBooking

        // THEN
        XCTAssertTrue(result, "Should be true when free breakfast promo code is present")
    }

    func testIsPromotionalBookingSiteWidePromoCodePresentReturnsTrue() {
        // GIVEN
        sut.appIncentivePromoCode = nil
        sut.freeBreakfastPromoCode = nil
        sut.siteWidePromoCode = "SITE"
        sut.userEnteredPromoCode = nil
        sut.rate = Constants.rateWithoutPromotionCode

        // WHEN
        let result = sut.isPromotionalBooking

        // THEN
        XCTAssertTrue(result, "Should be true when site wide promo code is present")
    }

    func testIsPromotionalBookingUserEnteredPromoCodePresentReturnsTrue() {
        // GIVEN
        sut.appIncentivePromoCode = nil
        sut.freeBreakfastPromoCode = nil
        sut.siteWidePromoCode = nil
        sut.userEnteredPromoCode = "USER"
        sut.rate = Constants.rateWithoutPromotionCode

        // WHEN
        let result = sut.isPromotionalBooking

        // THEN
        XCTAssertTrue(result)
    }

    func testIsPromotionalBookingRatePromotionCodePresentReturnsTrue() {
        // GIVEN
        sut.appIncentivePromoCode = nil
        sut.freeBreakfastPromoCode = nil
        sut.siteWidePromoCode = nil
        sut.userEnteredPromoCode = nil
        sut.rate = Constants.rateWithPromotionCode

        // WHEN
        let result = sut.isPromotionalBooking

        // THEN
        XCTAssertTrue(result, "Should be true when rate promotion code is present")
    }

    func testIsPromotionalBookingAllPromoCodesEmptyStringsReturnsFalse() {
        // GIVEN
        sut.appIncentivePromoCode = ""
        sut.freeBreakfastPromoCode = ""
        sut.siteWidePromoCode = ""
        sut.userEnteredPromoCode = ""
        sut.rate = Constants.rateWithEmptyPromotionCode

        // WHEN
        let result = sut.isPromotionalBooking

        // THEN
        XCTAssertFalse(result, "Should be false when all promo codes are empty")
    }
    
}
