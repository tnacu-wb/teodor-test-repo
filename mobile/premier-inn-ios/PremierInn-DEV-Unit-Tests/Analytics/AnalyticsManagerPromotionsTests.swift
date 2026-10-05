//
//  AnalyticsPromotionsTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 06/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn
@testable import SimpleNetwork

final class AnalyticsManagerPromotionsTests: XCTestCase {

    private var sut: AnalyticsManager!
    private var bookingDetails: BookingDetails!

    override func setUp() {
        super.setUp()
        sut = AnalyticsManager()
        bookingDetails = BookingDetails()
    }

    override func tearDown() {
        sut = nil
        bookingDetails = nil
        super.tearDown()
    }

    // MARK: - Constants

    private enum Constants {
        static let appIncentivePromoCode = "APP123"
        static let freeBreakfastPromoCode = "FREE_BREAKFAST"
        static let siteWidePromoCode = "FX30R"
        static let userEnteredPromoCode = "FX10R"

        static let siteWidePromoKind = "SITE_WIDE"
        static let userEnteredPromoKind = "UNIQUE"

        static let rateTag = "30% off"
    }

    // MARK: - Tests

    func testGetPromotionsAnalyticsDictWhenNotPromotionalBookingThenReturnsNil() {
        // GIVEN
        // No promo codes set

        // WHEN
        let result = sut.getPromotionsAnalyticsDict(with: bookingDetails)

        // THEN
        XCTAssertNil(result)
    }

    func testGetPromotionsAnalyticsDictWhenAppIncentivePromoExistsThenUsesItWithCorrectName() {
        // GIVEN
        bookingDetails.appIncentivePromoCode = Constants.appIncentivePromoCode

        // WHEN
        let result = sut.getPromotionsAnalyticsDict(with: bookingDetails)

        // THEN
        XCTAssertEqual(result?[PIAnalytics.Keys.promoCode] as? String, Constants.appIncentivePromoCode)
        XCTAssertEqual(result?[PIAnalytics.Keys.promoName] as? String, PIAnalytics.PromoNames.appIncentive)
        XCTAssertEqual(result?[PIAnalytics.Keys.promoCodeApplied] as? Bool, true)
    }

    func testGetPromotionsAnalyticsDictWhenFreeBreakfastPromoExistsThenUsesIt() {
        // GIVEN
        bookingDetails.freeBreakfastPromoCode = Constants.freeBreakfastPromoCode

        // WHEN
        let result = sut.getPromotionsAnalyticsDict(with: bookingDetails)

        // THEN
        XCTAssertEqual(result?[PIAnalytics.Keys.promoCode] as? String, Constants.freeBreakfastPromoCode)
        XCTAssertEqual(result?[PIAnalytics.Keys.promoName] as? String, PIAnalytics.PromoNames.freeBreakfast)
        XCTAssertEqual(result?[PIAnalytics.Keys.promoCodeApplied] as? Bool, true)
    }

    func testGetPromotionsAnalyticsDictWhenSiteWidePromoExistsThenUsesPromoKindAsName() {
        // GIVEN
        bookingDetails.siteWidePromoCode = Constants.siteWidePromoCode
        bookingDetails.promoKind = Constants.siteWidePromoKind

        // WHEN
        let result = sut.getPromotionsAnalyticsDict(with: bookingDetails)

        // THEN
        XCTAssertEqual(result?[PIAnalytics.Keys.promoCode] as? String, Constants.siteWidePromoCode)
        XCTAssertEqual(result?[PIAnalytics.Keys.promoName] as? String, Constants.siteWidePromoKind)
        XCTAssertEqual(result?[PIAnalytics.Keys.promoCodeApplied] as? Bool, true)
    }

    func testGetPromotionsAnalyticsDictWhenUserEnteredPromoExistsThenUsesPromoKindAsName() {
        // GIVEN
        bookingDetails.userEnteredPromoCode = Constants.userEnteredPromoCode
        bookingDetails.promoKind = Constants.userEnteredPromoKind

        // WHEN
        let result = sut.getPromotionsAnalyticsDict(with: bookingDetails)

        // THEN
        XCTAssertEqual(result?[PIAnalytics.Keys.promoCode] as? String, Constants.userEnteredPromoCode)
        XCTAssertEqual(result?[PIAnalytics.Keys.promoName] as? String, Constants.userEnteredPromoKind)
        XCTAssertEqual(result?[PIAnalytics.Keys.promoCodeApplied] as? Bool, true)
    }

    func testGetPromotionsAnalyticsDictWhenPromoRateTagsExistThenTheyAreAdded() {
        // GIVEN
        bookingDetails.appIncentivePromoCode = Constants.appIncentivePromoCode
        bookingDetails.promoRateTags = Constants.rateTag

        // WHEN
        let result = sut.getPromotionsAnalyticsDict(with: bookingDetails)

        // THEN
        XCTAssertEqual(result?[PIAnalytics.Keys.promoRateTags] as? String, Constants.rateTag, "Should be equal to \(Constants.rateTag)")
    }

    func testGetPromotionsAnalyticsDictWhenNoPromoCodesButHasPromoRateTagsThenReturnsOnlyRateTags() {
        // GIVEN
        let rate = Rate(dictionary: [
            "promotionCode": "RATE123"
        ])
        bookingDetails.rate = rate
        bookingDetails.promoRateTags = Constants.rateTag

        // WHEN
        let result = sut.getPromotionsAnalyticsDict(with: bookingDetails)

        // THEN
        XCTAssertNil(result?[PIAnalytics.Keys.promoCode])
        XCTAssertEqual(result?[PIAnalytics.Keys.promoRateTags] as? String, Constants.rateTag, "Should be equal to \(Constants.rateTag)")
    }
}
