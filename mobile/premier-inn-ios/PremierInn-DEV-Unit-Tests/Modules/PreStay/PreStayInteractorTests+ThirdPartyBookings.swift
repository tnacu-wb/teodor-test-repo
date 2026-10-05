//
//  PreStayInteractorTests+ThirdPartyBookings.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 18/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

extension PreStayInteractorTests {

    // MARK: - performPreStayChecks tests

    func testPerformPreStayChecksForThirdPartyBookingsForDERegCard() {
        // GIVEN mock Settings manager with FF set to true
        let mockSettingsManager = MockSettingsManager()
        mockSettingsManager.featureThirdPartyPrepaid = true

        // GIVEN input params with German brand, thirdPartyBooking and city tax
        let inputParams = setupPrestayParams(
            hotelBrand: .premierInnGermany,
            isDirect: false
        )

        let mockPresenter = MockPreStayPresenter()

        interactor = PreStayInteractor(
            preStayInputParams: inputParams,
            settingsManager: mockSettingsManager
        )

        interactor.output = mockPresenter

        let expectation = XCTestExpectation(description: #function)

        // WHEN
        interactor.performPreStayChecks { success in

            // THEN
            XCTAssertTrue(mockPresenter.goToGuestDetailsDERegCardCalled)
            XCTAssertFalse(mockPresenter.showCityTaxDisclaimerCalled)
            XCTAssertTrue(success)

            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3)
    }

    func testPerformPreStayChecksForThirdPartyBookingsForCityTaxDisclaimer() {
        // GIVEN mock Settings manager with FF set to true
        let mockSettingsManager = MockSettingsManager()
        mockSettingsManager.featureThirdPartyPrepaid = true

        // GIVEN input params with UK brand, thirdPartyBooking and payment actions response has city tax
        let inputParams = setupPrestayParams(
            hotelBrand: .premierInn,
            isDirect: false,
            paymentActions: .cityTaxResponse
        )

        let mockPresenter = MockPreStayPresenter()

        interactor = PreStayInteractor(
            preStayInputParams: inputParams,
            settingsManager: mockSettingsManager
        )

        interactor.output = mockPresenter

        let expectation = XCTestExpectation(description: #function)

        // WHEN
        interactor.performPreStayChecks { success in

            // THEN
            XCTAssertFalse(mockPresenter.goToGuestDetailsDERegCardCalled)
            XCTAssertTrue(mockPresenter.showCityTaxDisclaimerCalled)
            XCTAssertTrue(success)

            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3)
    }

    func testPerformPreStayChecksForThirdPartyBookingsForCityTaxDisclaimerWhenCityTaxNil() {
        // GIVEN mock Settings manager with FF set to true
        let mockSettingsManager = MockSettingsManager()
        mockSettingsManager.featureThirdPartyPrepaid = true

        // GIVEN input params with UK brand, thirdPartyBooking and paymentActions set to nil
        let inputParams = setupPrestayParams(
            hotelBrand: .premierInn,
            isDirect: false,
            paymentActions: nil
        )

        let mockDataProvider = MockPreStayDataProvider()
        let mockPresenter = MockPreStayPresenter()

        interactor = PreStayInteractor(
            dataProvider: mockDataProvider,
            preStayInputParams: inputParams,
            settingsManager: mockSettingsManager
        )

        interactor.output = mockPresenter

        let expectation = XCTestExpectation(description: #function)

        // WHEN
        interactor.performPreStayChecks { success in

            // THEN
            XCTAssertFalse(mockPresenter.goToGuestDetailsDERegCardCalled)
            XCTAssertFalse(mockPresenter.showCityTaxDisclaimerCalled)
            XCTAssertTrue(mockDataProvider.isConfirmPreCheckInOutCalled)
            XCTAssertTrue(success)

            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3)
    }

    // MARK: - priceBreakdownViewModel tests

    func testPriceBreakdownViewModelForThirPartyBookingsForCityTax() {
        // GIVEN mock Settings manager with FF set to true
        let mockSettingsManager = MockSettingsManager()

        // GIVEN input params with UK brand, thirdPartyBooking and payment actions response has city tax
        let inputParams = setupPrestayParams(
            hotelBrand: .premierInn,
            isDirect: false,
            paymentActions: .cityTaxResponse
        )

        let mockPresenter = MockPreStayPresenter()

        interactor = PreStayInteractor(
            preStayInputParams: inputParams,
            settingsManager: mockSettingsManager
        )

        interactor.output = mockPresenter

        // WHEN
        let result = interactor.priceBreakdownViewModel

        // THEN
        XCTAssertEqual(result.ctaTitle, PILocalizedString("Continue"))
        XCTAssertEqual(result.totalValue, "£13.00")
        XCTAssertEqual(result.items.first?.name, PILocalizedString("ciolCityTaxDisclaimerTitle"))
        XCTAssertEqual(result.items.first?.value, CiolPaymentActionsResponse.cityTaxResponse.cityTax)
        XCTAssertEqual(result.items.first?.quantity, 1)
    }

    func testPriceBreakdownViewModelForThirPartyBookingsForOutstandingBalance() {
        // GIVEN mock Settings manager with FF set to true
        let mockSettingsManager = MockSettingsManager()

        // GIVEN input params with UK brand, thirdPartyBooking and payment actions response has city tax
        let inputParams = setupPrestayParams(
            hotelBrand: .premierInn,
            isDirect: false,
            paymentActions: .outstandingBalanceResponse
        )

        let mockPresenter = MockPreStayPresenter()

        interactor = PreStayInteractor(
            preStayInputParams: inputParams,
            settingsManager: mockSettingsManager
        )

        interactor.output = mockPresenter

        // WHEN
        let result = interactor.priceBreakdownViewModel

        // THEN
        XCTAssertEqual(result.ctaTitle, PILocalizedString("Continue"))
        XCTAssertEqual(result.totalValue, "£133.00")
        XCTAssertEqual(result.items.first?.name, PILocalizedString("preStayOutstandingBalance"))
        XCTAssertEqual(result.items.first?.value, CiolPaymentActionsResponse.outstandingBalanceResponse.outstandingBalance)
        XCTAssertEqual(result.items.first?.quantity, 1)
    }
}
