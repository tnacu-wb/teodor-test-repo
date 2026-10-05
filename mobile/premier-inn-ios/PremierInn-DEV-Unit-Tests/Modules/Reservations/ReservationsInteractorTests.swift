//
//  ReservationsInteractorTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 08/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

final class ReservationsInteractorTests: XCTestCase {

    func testFetchCiolPaymentActionsWhenSuccess() {
        // GIVEN sut and stubbed response
        let dataProvider = MockReservationsInteractorDataProvider()
        dataProvider.stubbedCiolPaymentActionsResponse = (CiolPaymentActionsResponse.cityTaxResponse, nil)

        let sut = ReservationsInteractor(with: dataProvider)
        sut.stayToBeCheckedId = .mock

        let expectation = XCTestExpectation(description: #function)

        // WHEN
        sut.fetchCiolPaymentActions(
            reservation: .mock,
            hotel: .mock,
            basketReference: "SOME_BASKET_REFERENCE"
        ) { success, inputParams in
           // THEN
            XCTAssertTrue(success)
            XCTAssertNotNil(inputParams)
            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3)
    }

    func testFetchCiolPaymentActionsWhenComputeParamsFails() {
        // GIVEN sut and stubbed response
        let dataProvider = MockReservationsInteractorDataProvider()
        dataProvider.stubbedCiolPaymentActionsResponse = (CiolPaymentActionsResponse.cityTaxResponse, nil)

        let sut = ReservationsInteractor(with: dataProvider)

        let expectation = XCTestExpectation(description: #function)

        // WHEN
        sut.fetchCiolPaymentActions(
            reservation: .mock,
            hotel: .mock,
            basketReference: "SOME_BASKET_REFERENCE"
        ) { success, inputParams in
           // THEN
            XCTAssertFalse(success)
            XCTAssertNil(inputParams)
            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3)
    }

    func testFetchCiolPaymentActionsWhenError() {
        // GIVEN sut and stubbed response
        let dataProvider = MockReservationsInteractorDataProvider()
        dataProvider.stubbedCiolPaymentActionsResponse = (nil, NSError(domain: "Error", code: 404))

        let sut = ReservationsInteractor(with: dataProvider)

        let expectation = XCTestExpectation(description: #function)

        // WHEN
        sut.fetchCiolPaymentActions(
            reservation: .mock,
            hotel: .mock,
            basketReference: "SOME_BASKET_REFERENCE"
        ) { success, inputParams in
           // THEN
            XCTAssertFalse(success)
            XCTAssertNil(inputParams)
            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3)
    }

}
