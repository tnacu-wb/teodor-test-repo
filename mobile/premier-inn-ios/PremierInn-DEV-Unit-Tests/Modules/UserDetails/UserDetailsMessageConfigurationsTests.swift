//
//  UserDetailsMessageConfigurationsTests.swift
//  PremierInn
//
//  Created by Clint Mengolli on 07/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn
@testable import SimpleNetwork

private struct MockBookingDetailsMessageContext: BookingDetailsMessageContextType {
    var cityTaxForLeisure: Bool?
    var cityTaxForBusiness: Bool?
    var totalCostWithoutCityTax: Cost?
}

final class UserDetailsMessageConfigurationsTests: XCTestCase {

    // MARK: - Marketing Model Tests

    func test_marketingModel_isNil_whenScopeIsNotUserPreferencesAndEmailIsSuppressed() {
        let sut = UserDetailsMessageConfigurations(
            suppressEmailSection: true,
            isMarketingSwitchEnabled: true,
            scope: .bookingFlow
        )
        
        XCTAssertNil(sut.marketingModel)
    }

    func test_marketingModel_isNotNil_whenInUserPreferences_regardlessOfSuppression() {
        let sut = UserDetailsMessageConfigurations(
            suppressEmailSection: true,
            isMarketingSwitchEnabled: true,
            scope: .userPreferences
        )
        
        XCTAssertNotNil(sut.marketingModel)
        XCTAssertEqual(sut.marketingModel?.isActive, true)
    }

    // MARK: - Trip Purpose / City Tax Tests

    func test_tripPurposeModel_isNil_whenCityTaxDataIsMissing() {
        let emptyContext = MockBookingDetailsMessageContext(cityTaxForLeisure: nil, cityTaxForBusiness: nil)
        let sut = createMock(context: emptyContext)

        XCTAssertNil(sut.tripPurposeModel)
    }

    func test_tripPurposeModel_isNil_whenBothTaxesAreFalse() {
        let context = MockBookingDetailsMessageContext(cityTaxForLeisure: false, cityTaxForBusiness: false)
        let sut = createMock(context: context)

        XCTAssertNil(sut.tripPurposeModel)
    }

    func test_tripPurposeModel_bothTrue_returnsAllStaysMessage() {
        let context = MockBookingDetailsMessageContext(cityTaxForLeisure: true, cityTaxForBusiness: true)
        let sut = createMock(context: context)
        let model = sut.tripPurposeModel

        XCTAssertNotNil(model)

        let leisureMessage = model?.leisureMessages?.first?.message.string
        let businessMessage = model?.businessMessages?.first?.message.string

        XCTAssertEqual(leisureMessage, PILocalizedString("userDetailsCityTaxAllStays"))
        XCTAssertEqual(businessMessage, PILocalizedString("userDetailsCityTaxAllStays"))
    }

    func test_tripPurposeModel_leisureExempt_businessTaxed() {
        let context = MockBookingDetailsMessageContext(cityTaxForLeisure: false, cityTaxForBusiness: true)
        let sut = createMock(context: context)
        let model = sut.tripPurposeModel

        let leisureMessage = model?.leisureMessages?.first?.message.string

        XCTAssertEqual(model?.leisureMessages?.count, 1)
        XCTAssertEqual(model?.businessMessages?.count, 2)
        XCTAssertEqual(leisureMessage, PILocalizedString("userDetailsLeisureExemptMessage"))
    }

    func test_tripPurposeModel_businessExempt_leisureTaxed() {
        let context = MockBookingDetailsMessageContext(
            cityTaxForLeisure: true,
            cityTaxForBusiness: false
        )
        let sut = createMock(context: context)
        let model = sut.tripPurposeModel

        let businessMessage = model?.businessMessages?.first?.message.string

        XCTAssertEqual(model?.businessMessages?.count, 1)
        XCTAssertEqual(model?.leisureMessages?.count, 2)
        XCTAssertEqual(businessMessage, PILocalizedString("userDetailsBusinessExemptMessage"))
    }

}

private extension UserDetailsMessageConfigurationsTests {
    func createMock(context: BookingDetailsMessageContextType) -> UserDetailsMessageConfigurations {
        return UserDetailsMessageConfigurations(
            suppressEmailSection: false,
            isMarketingSwitchEnabled: true,
            bookingContext: context,
            scope: .bookingFlow
        )
    }
}
