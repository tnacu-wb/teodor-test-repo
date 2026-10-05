//
//  UpsellsInteractorTests.swift
//  PremierInnTests
//
//  Created by Santa Gurung on 06/09/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

final class UpsellsInteractorTests: XCTestCase {

    var upsellsInteractor: UpsellsInteractor!
    var mealDealUpsell: UpsellItem!
    var continentalUpsell: UpsellItem!
    var checkinCheckoutUpsell: UpsellItem!

    private var mockAnalyticsManager = MockAnalyticsManager()

    override func setUp() {

        mealDealUpsell = try! UpsellItem(dictionary: [
            "operaId": "MDP",
            "code": "17",
            "legend": "Meal Deal",
            "foodUpsell": true,
            "freeBreakfastTrigger": true,
            "freeBreakfastCode": "BFCHDF",
            "freeBreakfastMaxPerMeal": 2,
            "price": ["amount": "26.49",
                      "currency": "GBP"]
        ])
        continentalUpsell = try! UpsellItem(dictionary: [
            "operaId": "BFADCT",
            "code": "12",
            "legend": "Continental Breakfast",
            "foodUpsell": true,
            "freeBreakfastTrigger": false,
            "price": ["amount": "9.99",
                      "currency": "GBP"]
        ])
        checkinCheckoutUpsell = try! UpsellItem(dictionary: [
            "operaId": "HSCKIN",
            "legend": "Early check-in",
            "isExtraUpsell": true,
            "price": ["amount": "10.0",
                      "currency": "GBP"]
        ])
    }

    var interactor: UpsellsInteractor {
        let hotel = try! Hotel(dictionary: [
            "hotelCode": "BRIPTI",
            "hotelInfo": [
                "name": "PremierInn Brighton",
                "address": ["postcode": "BN1 1RE", "addressline1": "144 North Street", "addressline2": "Brighton", "addressline3": "East Sussex", "country": "United Kingdom (the)"]
            ]])
        var criteria = Criteria()
        criteria.nights = 1
        let bookingDetails = BookingDetails()
        bookingDetails.criteria = criteria

        let room = Room()
        room.adults = 2
        room.children = 2

        return UpsellsInteractor(
            with: [room],
            hotel: hotel,
            upsellOptions: [mealDealUpsell, continentalUpsell],
            extraUpsells: [checkinCheckoutUpsell],
            and: bookingDetails,
            analyticsManager: mockAnalyticsManager
        )
    }

    func calculateCostSummary(nights: Int, adults: Int, upsell: UpsellItem) -> String {
        let hotel = try! Hotel(dictionary: [
            "hotelCode": "BRIPTI",
            "hotelInfo": [
                "name": "PremierInn Brighton",
                "address": ["postcode": "BN1 1RE", "addressline1": "144 North Street", "addressline2": "Brighton", "addressline3": "East Sussex", "country": "United Kingdom (the)"]
            ]])
        var criteria = Criteria()
        criteria.nights = nights
        let bookingDetails = BookingDetails()
        bookingDetails.criteria = criteria

        let room = Room()
        room.adults = adults

        upsellsInteractor = UpsellsInteractor(
            with: [room],
            hotel: hotel,
            upsellOptions: [mealDealUpsell, continentalUpsell],
            extraUpsells: [checkinCheckoutUpsell],
            and: bookingDetails
        )
        let roomIndex = 0

        let costSummary = upsellsInteractor.costSummary(for: roomIndex, upsell: upsell)
        return costSummary.string
    }

    func testCostSummaryForMealDealUpsellForOneNightTwoAdults() {
        let costSummary = calculateCostSummary(nights: 1, adults: 2, upsell: mealDealUpsell)
        XCTAssertEqual(costSummary, "£26.49 per day")
    }

    func testCostSummaryForMealDealUpsellForTwoNightsTwoAdults() {
        let costSummary = calculateCostSummary(nights: 2, adults: 2, upsell: mealDealUpsell)
        XCTAssertEqual(costSummary, "£26.49 per day")
    }

    func testCostSummaryForMealDealUpsellForOneNightOneAdult() {
        let costSummary = calculateCostSummary(nights: 1, adults: 1, upsell: mealDealUpsell)
        XCTAssertEqual(costSummary, "£26.49 per day")
    }

    func testCostSummaryForContinentalForTwoNightsTwoAdults() {
        let costSummary = calculateCostSummary(nights: 2, adults: 2, upsell: continentalUpsell)
        XCTAssertEqual(costSummary, "£9.99 per day")
    }

    func testCostSummaryForContinentalForOneNightOneAdult() {
        let costSummary = calculateCostSummary(nights: 1, adults: 1, upsell: continentalUpsell)
        XCTAssertEqual(costSummary, "£9.99 per day")
    }

    func testCostSummaryForExtraUpsellForTwoNightsTwoAdults() {
        let costSummary = calculateCostSummary(nights: 2, adults: 2, upsell: checkinCheckoutUpsell)
        XCTAssertEqual(costSummary, "£10.00")
    }

    func testCostSummaryForExtraUpsellForOneNightTwoAdults() {
        let costSummary = calculateCostSummary(nights: 1, adults: 2, upsell: checkinCheckoutUpsell)
        XCTAssertEqual(costSummary, "£10.00")
    }


    func testStepperIncrease() {

        self.upsellsInteractor = interactor
        self.upsellsInteractor.selected(upsellAt: 1, in: 0, numberOfSelection: 2)

        XCTAssertEqual(self.upsellsInteractor.bookingDetails?.roomMealCombos?.first!.quantity, 2)
        XCTAssertEqual(self.upsellsInteractor.bookingDetails?.roomMealCombos?.first!.roomNumber, 0)
        XCTAssertEqual(self.upsellsInteractor.bookingDetails?.roomMealCombos?.first!.meal.id, "BFADCT")

    }

    func testMaxCountForRoom() {

        self.upsellsInteractor = interactor
        self.upsellsInteractor.selected(upsellAt: 1, in: 0, numberOfSelection: 2)

        XCTAssertEqual(self.upsellsInteractor.viewModel?.roomViewModels.first?.upsellItems![0].maxSelected, 0)
        XCTAssertEqual(self.upsellsInteractor.viewModel?.roomViewModels.first?.upsellItems![1].maxSelected, 2)

        self.upsellsInteractor.selected(upsellAt: 1, in: 0, numberOfSelection: 1)
        self.upsellsInteractor.selected(upsellAt: 0, in: 0, numberOfSelection: 1)

        XCTAssertEqual(self.upsellsInteractor.viewModel?.roomViewModels.first?.upsellItems![0].maxSelected, 1)
        XCTAssertEqual(self.upsellsInteractor.viewModel?.roomViewModels.first?.upsellItems![1].maxSelected, 1)
    }

    func testUpsellSummaryStrings() {

        self.upsellsInteractor = interactor

        self.upsellsInteractor.selected(upsellAt: 0, in: 0, numberOfSelection: 1)
        self.upsellsInteractor.selected(upsellAt: 1, in: 0, numberOfSelection: 1)
        XCTAssertEqual(self.upsellsInteractor.viewModel?.roomViewModels.first!.upsellsSummary!, "1 x Meal Deal (Breakfast & Dinner) - £26.49\n1 x Continental Breakfast - £9.99\nKids eat free breakfast")


        self.upsellsInteractor.selected(upsellAt: 0, in: 0, numberOfSelection: 0)
        self.upsellsInteractor.selected(upsellAt: 1, in: 0, numberOfSelection: 2)
        XCTAssertEqual(self.upsellsInteractor.viewModel?.roomViewModels.first!.upsellsSummary!, "2 x Continental Breakfast - £9.99")
    }

    func testPromotionsAnalytics() {
        // GIVEN
        self.upsellsInteractor = interactor
        let expectedDict = MockAnalyticsManager.mockAnalyticsDict
        let expectedResultantString = expectedDict[MockAnalyticsManager.MockDictDataKeyValues.someAnalyticsKey] as? String

        // WHEN
        let actualDict = upsellsInteractor.promotionsAnalytics
        let actualResultantString = actualDict?[MockAnalyticsManager.MockDictDataKeyValues.someAnalyticsKey] as? String

        // THEN
        XCTAssertEqual(actualResultantString, expectedResultantString, "Should be equal")
    }
}
