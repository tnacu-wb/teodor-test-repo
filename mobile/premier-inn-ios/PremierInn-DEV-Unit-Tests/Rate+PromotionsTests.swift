//
//  Rate+PromotionsTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 08/07/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Testing
@testable import SimpleNetwork
@testable import PremierInn

// MARK: - RatePromotionsTests

struct RatePromotionsTests {

    // MARK: - Constants

    private enum Constants {
        static let classification = "FLEXRATE"
        static let plan = "FLEXRATE"
        static let description = "Some description"
        static let text = "Some text"

        enum PromoCode {
            static let employee = SimpleNetwork.Constants.EmployeeOffer.rateCode
            static let appIncentive = PILocalizedString("appIncentivePromoCode")
            static let freeBreakfast = "PREBF"
            static let siteWide = "SITE10"
            static let userEntered = "USER10"
            static let unknown = "UNKNOWN"
        }

        enum RateTag {
            static let freeBreakfast = "Free breakfast"
            static let siteWideDiscount = "Site-wide discount"
            static let userDiscount = "User discount"
        }
    }

    // MARK: - Employee offer

    @Test
    func promotionTagReturnsEmployeeRateTagWhenClassificationIsEmployeeOffer() {
        // GIVEN
        let sut = makeRate(
            classification: SimpleNetwork.Constants.EmployeeOffer.rateClassification,
            promotionCode: Constants.PromoCode.employee
        )

        // WHEN
        let result = sut.promotionTag(
            for: .premierInn,
            settingsManager: MockSettingsManager(),
            bookingDetails: MockBookingDetails()
        )

        // THEN
        #expect(result == PILocalizedString("hotelDetailsEmployeeRate"))
    }

    // MARK: - App incentive

    @Test
    func promotionTagReturnsAppDiscountTagWhenPromotionCodeMatchesAppIncentivePromoCode() {
        // GIVEN
        let settingsManager = MockSettingsManager()

        let sut = makeRate(
            promotionCode: Constants.PromoCode.appIncentive
        )

        // WHEN
        let result = sut.promotionTag(
            for: .premierInn,
            settingsManager: settingsManager,
            bookingDetails: MockBookingDetails()
        )

        // THEN
        #expect(result == PILocalizedString("appDiscountTag"))
    }

    // MARK: - Free breakfast

    @Test
    func promotionTagReturnsFirstRateTagWhenPromotionCodeMatchesFreeBreakfastPromoCode() {
        // GIVEN
        let settingsManager = MockSettingsManager()
        settingsManager.ratesContent = [
            .mock(
                classification: Constants.classification,
                tags: [Constants.RateTag.freeBreakfast]
            )
        ]

        let bookingDetails = MockBookingDetails()
        bookingDetails.freeBreakfastPromoCode = Constants.PromoCode.freeBreakfast

        let sut = makeRate(
            promotionCode: Constants.PromoCode.freeBreakfast
        )

        // WHEN
        let result = sut.promotionTag(
            for: .premierInn,
            settingsManager: settingsManager,
            bookingDetails: bookingDetails
        )

        // THEN
        #expect(result == Constants.RateTag.freeBreakfast)
    }

    // MARK: - Site wide promo

    @Test
    func promotionTagReturnsFirstRateTagWhenPromotionCodeMatchesSiteWidePromoCode() {
        // GIVEN
        let settingsManager = MockSettingsManager()
        settingsManager.ratesContent = [
            .mock(
                classification: Constants.classification,
                tags: [Constants.RateTag.siteWideDiscount]
            )
        ]
        settingsManager.siteWidePromotionContent = .init(
            title: "Site wide promotion",
            subtitle: "Some promotion going on at the moment",
            promotionCode: Constants.PromoCode.siteWide,
            urlString: "some url string"
        )

        let sut = makeRate(
            promotionCode: Constants.PromoCode.siteWide
        )

        // WHEN
        let result = sut.promotionTag(
            for: .premierInn,
            settingsManager: settingsManager,
            bookingDetails: MockBookingDetails()
        )

        // THEN
        #expect(result == Constants.RateTag.siteWideDiscount)
    }

    // MARK: - User entered

    @Test
    func promotionTagReturnsFirstRateTagWhenPromotionCodeMatchesUserEnteredPromoCode() {
        // GIVEN
        let settingsManager = MockSettingsManager()
        settingsManager.ratesContent = [
            .mock(
                classification: Constants.classification,
                tags: [Constants.RateTag.userDiscount]
            )
        ]

        let bookingDetails = MockBookingDetails()
        bookingDetails.userEnteredPromoCode = Constants.PromoCode.userEntered

        let sut = makeRate(
            promotionCode: Constants.PromoCode.userEntered
        )

        // WHEN
        let result = sut.promotionTag(
            for: .premierInn,
            settingsManager: settingsManager,
            bookingDetails: bookingDetails
        )

        // THEN
        #expect(result == Constants.RateTag.userDiscount)
    }

    // MARK: - Other cases

    @Test
    func promotionTagReturnsNilWhenClassificationIsNil() {
        // GIVEN
        let sut = makeRate(
            classification: nil,
            promotionCode: Constants.PromoCode.freeBreakfast
        )

        // WHEN
        let result = sut.promotionTag(
            for: .premierInn,
            settingsManager: MockSettingsManager(),
            bookingDetails: MockBookingDetails()
        )

        // THEN
        #expect(result == nil)
    }

    @Test
    func promotionTagReturnsNilWhenPromotionCodeIsNil() {
        // GIVEN
        let sut = makeRate(
            classification: Constants.classification,
            promotionCode: nil
        )

        // WHEN
        let result = sut.promotionTag(
            for: .premierInn,
            settingsManager: MockSettingsManager(),
            bookingDetails: MockBookingDetails()
        )

        // THEN
        #expect(result == nil)
    }

    @Test
    func promotionTagReturnsFirstNonNilRateTagWhenRateTagsContainNilValues() {
        // GIVEN
        let settingsManager = MockSettingsManager()
        settingsManager.ratesContent = [
            .mock(
                classification: Constants.classification,
                tags: [nil, Constants.RateTag.freeBreakfast]
            )
        ]

        let bookingDetails = MockBookingDetails()
        bookingDetails.freeBreakfastPromoCode = Constants.PromoCode.freeBreakfast

        let sut = makeRate(
            promotionCode: Constants.PromoCode.freeBreakfast
        )

        // WHEN
        let result = sut.promotionTag(
            for: .premierInn,
            settingsManager: settingsManager,
            bookingDetails: bookingDetails
        )

        // THEN
        #expect(result == Constants.RateTag.freeBreakfast)
    }

    @Test
    func promotionTagReturnsNilWhenPromotionCodeDoesNotMatchAnyPromoCode() {
        // GIVEN
        let settingsManager = MockSettingsManager()

        let bookingDetails = MockBookingDetails()
        bookingDetails.freeBreakfastPromoCode = Constants.PromoCode.freeBreakfast
        bookingDetails.userEnteredPromoCode = Constants.PromoCode.userEntered

        let sut = makeRate(
            promotionCode: Constants.PromoCode.unknown
        )

        // WHEN
        let result = sut.promotionTag(
            for: .premierInn,
            settingsManager: settingsManager,
            bookingDetails: bookingDetails
        )

        // THEN
        #expect(result == nil)
    }
}

// MARK: - Helpers

private extension RatePromotionsTests {
    func makeRate(
        classification: String? = Constants.classification,
        promotionCode: String? = nil
    ) -> Rate {
        Rate(dictionary: [
            "plan": Constants.plan as Any,
            "classification": classification as Any,
            "description": Constants.description as Any,
            "text": Constants.text as Any,
            "cost": [
                "amount": 11,
                "currencyCode": "GBP"
            ],
            "promotionCode": promotionCode as Any
        ])
    }
}
