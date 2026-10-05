//
//  CiolReviewAndPayInteractor+backgroundChargeTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 29/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork
@testable import PremierInn

extension CiolReviewAndPayInteractorTests {

    // MARK: - handleBackgroundChargeIfRequired tests

    func testHandleBackgroundChargeIfRequiredWhenFeatureFlagOff() {
        // GIVEN
        let settingsManager = MockSettingsManager()
        settingsManager.featureThirdPartyPrepaid = false

        let bookingDetails = MockBookingDetails()
        bookingDetails.isCiolBackgroundChargePerformedSuccessfully = false

        let inputParams = createInputParams(paymentActions: .backgroundChargeResponse)

        let dataProvider = MockCiolReviewAndPayDataProvider()

        let sut = CiolReviewAndPayInteractor(
            dataProvider: dataProvider,
            inputParams: inputParams,
            settingsManager: settingsManager
        )
        sut.bookingDetails = bookingDetails

        let expectation = expectation(description: #function)

        // WHEN
        sut.handleBackgroundChargeIfRequired { isSuccess in

            // THEN
            XCTAssertTrue(isSuccess)
            XCTAssertFalse(sut.bookingDetails.isCiolBackgroundChargePerformedSuccessfully)
            XCTAssertFalse(dataProvider.isCiolBackgroundChargeCalled)
            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3)
    }

    func testHandleBackgroundChargeIfRequiredWhenBackgroundChargeAlreadyPerformed() {
        // GIVEN
        let settingsManager = MockSettingsManager()
        settingsManager.featureThirdPartyPrepaid = true

        let bookingDetails = MockBookingDetails()
        bookingDetails.basketReference = "123"
        bookingDetails.token = "abc"

        // Already performed
        bookingDetails.isCiolBackgroundChargePerformedSuccessfully = true

        let inputParams = createInputParams(paymentActions: .backgroundChargeResponse)

        let dataProvider = MockCiolReviewAndPayDataProvider()

        let sut = CiolReviewAndPayInteractor(
            dataProvider: dataProvider,
            inputParams: inputParams,
            settingsManager: settingsManager
        )
        sut.bookingDetails = bookingDetails

        let expectation = expectation(description: #function)

        // WHEN
        sut.handleBackgroundChargeIfRequired { isSuccess in
            // THEN
            XCTAssertTrue(isSuccess)
            XCTAssertFalse(dataProvider.isCiolBackgroundChargeCalled)
            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3)
    }

    func testHandleBackgroundChargeIfRequiredWhenPaymentActionsDoesNotNeedBackgroundCharge() {
        // GIVEN
        let settingsManager = MockSettingsManager()
        settingsManager.featureThirdPartyPrepaid = true

        let bookingDetails = MockBookingDetails()
        bookingDetails.isCiolBackgroundChargePerformedSuccessfully = false
        bookingDetails.basketReference = "123"
        bookingDetails.token = "abc"

        let inputParams = createInputParams(paymentActions: .outstandingBalanceResponse)

        let dataProvider = MockCiolReviewAndPayDataProvider()

        let sut = CiolReviewAndPayInteractor(
            dataProvider: dataProvider,
            inputParams: inputParams,
            settingsManager: settingsManager
        )
        sut.bookingDetails = bookingDetails

        let expectation = expectation(description: #function)

        // WHEN
        sut.handleBackgroundChargeIfRequired { isSuccess in
            // THEN
            XCTAssertTrue(isSuccess)
            XCTAssertFalse(dataProvider.isCiolBackgroundChargeCalled)
            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3)
    }

    func testHandleBackgroundChargeWhenBasketReferenceAndTokenAreNil() {
        // GIVEN
        let settingsManager = MockSettingsManager()
        settingsManager.featureThirdPartyPrepaid = true

        let bookingDetails = MockBookingDetails()
        bookingDetails.basketReference = nil
        bookingDetails.token = nil

        let inputParams = createInputParams(paymentActions: .backgroundChargeResponse)
        let dataProvider = MockCiolReviewAndPayDataProvider()

        let sut = CiolReviewAndPayInteractor(
            dataProvider: dataProvider,
            inputParams: inputParams,
            settingsManager: settingsManager
        )
        sut.bookingDetails = bookingDetails

        let expectation = expectation(description: #function)

        // WHEN
        sut.handleBackgroundChargeIfRequired { isSuccess in
            // THEN
            XCTAssertFalse(isSuccess)
            XCTAssertFalse(sut.bookingDetails.isCiolBackgroundChargePerformedSuccessfully)
            XCTAssertFalse(dataProvider.isCiolBackgroundChargeCalled)
            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3)
    }

    func testHandleBackgroundChargeWhenNetworkError() {
        // GIVEN
        let settingsManager = MockSettingsManager()
        settingsManager.featureThirdPartyPrepaid = true

        let bookingDetails = MockBookingDetails()
        bookingDetails.basketReference = "123"
        bookingDetails.token = "token"

        let inputParams = createInputParams(paymentActions: .backgroundChargeResponse)

        let dataProvider = MockCiolReviewAndPayDataProvider()
        dataProvider.mockedCiolBackgroundChargeResult = (nil, NSError(domain: "Error", code: 401))

        let sut = CiolReviewAndPayInteractor(
            dataProvider: dataProvider,
            inputParams: inputParams,
            settingsManager: settingsManager
        )

        sut.bookingDetails = bookingDetails

        let expectation = expectation(description: #function)

        // WHEN
        sut.handleBackgroundChargeIfRequired { isSuccess in

            // THEN
            XCTAssertFalse(isSuccess)
            XCTAssertFalse(sut.bookingDetails.isCiolBackgroundChargePerformedSuccessfully)
            XCTAssertTrue(dataProvider.isCiolBackgroundChargeCalled)
            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3)
    }

    func testHandleBackgroundChargeWhenNilResponse() {
        // GIVEN
        let settingsManager = MockSettingsManager()
        settingsManager.featureThirdPartyPrepaid = true

        let bookingDetails = MockBookingDetails()
        bookingDetails.basketReference = "123"
        bookingDetails.token = "token"

        let inputParams = createInputParams(paymentActions: .backgroundChargeResponse)

        let dataProvider = MockCiolReviewAndPayDataProvider()
        dataProvider.mockedCiolBackgroundChargeResult = (nil, nil)

        let sut = CiolReviewAndPayInteractor(
            dataProvider: dataProvider,
            inputParams: inputParams,
            settingsManager: settingsManager
        )

        sut.bookingDetails = bookingDetails

        let expectation = expectation(description: #function)

        // WHEN
        sut.handleBackgroundChargeIfRequired { isSuccess in

            // THEN
            XCTAssertFalse(isSuccess)
            XCTAssertFalse(sut.bookingDetails.isCiolBackgroundChargePerformedSuccessfully)
            XCTAssertTrue(dataProvider.isCiolBackgroundChargeCalled)
            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3)
    }

    func testHandleBackgroundChargeWhenSuccess() {
        // GIVEN
        let settingsManager = MockSettingsManager()
        settingsManager.featureThirdPartyPrepaid = true

        let bookingDetails = MockBookingDetails()
        bookingDetails.basketReference = "123"
        bookingDetails.token = "token"

        let inputParams = createInputParams(paymentActions: .backgroundChargeResponse)

        let dataProvider = MockCiolReviewAndPayDataProvider()
        dataProvider.mockedCiolBackgroundChargeResult = (
            CiolBackgroundChargeResponse(basketReference: "ref"),
            nil
        )

        let sut = CiolReviewAndPayInteractor(
            dataProvider: dataProvider,
            inputParams: inputParams,
            settingsManager: settingsManager
        )

        sut.bookingDetails = bookingDetails

        let expectation = expectation(description: #function)

        // WHEN
        sut.handleBackgroundChargeIfRequired { isSuccess in

            // THEN
            XCTAssertTrue(isSuccess)
            XCTAssertTrue(sut.bookingDetails.isCiolBackgroundChargePerformedSuccessfully)
            XCTAssertTrue(dataProvider.isCiolBackgroundChargeCalled)
            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3)
    }
}
