//
//  BookingConfirmationInteractorTests+fetchWalletPass.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 21/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import PassKit
import XCTest
@testable import SimpleNetwork
@testable import PremierInn

extension BookingConfirmationInteractorTests {
    // MARK: - fetchWalletPass tests

    func testWalletPassWhenPassCachedAndWalletManagerDoesNotContainsPass() {
        // GIVEN stay
        let stay = getStay()

        // GIVEN mock pass manager and mocked result is false
        let walletPassManager = MockPassManager()
        walletPassManager.containsPassResult = false

        // GIVEN set up the sut
        sut = BookingConfirmationInteractor(
            summary: stay,
            isBookingFlowComplete: false,
            walletPassManager: walletPassManager
        )

        // GIVEN mock data provider
        let output = MockBookingConfirmationInteractorOutput()
        sut.dataProvider = output

        // GIVEN sut has a pass
        sut.pass = PKPass()

        // GIVEN expectation
        let expectation = XCTestExpectation(description: #function)

        // WHEN fetchWalletPass is called
        sut.fetchWalletPass { result in
            // THEN
            XCTAssertEqual(result, .fetchSuccessful(PKPass()))

            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3.0)
    }

    func testWalletPassWhenPassCachedAndWalletManagerContainsPass() {
        // GIVEN stay
        let stay = getStay()

        // GIVEN mock pass manager and mocked result is false
        let walletPassManager = MockPassManager()
        walletPassManager.containsPassResult = true

        // GIVEN set up the sut
        sut = BookingConfirmationInteractor(
            summary: stay,
            isBookingFlowComplete: false,
            walletPassManager: walletPassManager
        )

        // GIVEN mock data provider
        let output = MockBookingConfirmationInteractorOutput()
        sut.dataProvider = output

        // GIVEN sut has a pass
        sut.pass = PKPass()

        // GIVEN expectation
        let expectation = XCTestExpectation(description: #function)

        // WHEN fetchWalletPass is called
        sut.fetchWalletPass { result in
            // THEN
            XCTAssertEqual(result, .containsPass)

            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3.0)
    }

    func testWalletPassWhenPassNotCachedAndDataProviderError() {
        // GIVEN stay
        let stay = getStay()

        // GIVEN mock pass manager and mocked result is false
        let walletPassManager = MockPassManager()
        walletPassManager.containsPassResult = true

        // GIVEN set up the sut
        sut = BookingConfirmationInteractor(
            summary: stay,
            isBookingFlowComplete: false,
            walletPassManager: walletPassManager
        )

        // GIVEN sut does not have a pass
        sut.pass = nil

        // GIVEN mock data provider with a failed result
        let output = MockBookingConfirmationInteractorOutput()
        let outputError = NSError(domain: "SomeError", code: 202)
        output.loadWalletPassResult = (nil, outputError)
        sut.dataProvider = output

        // GIVEN expectation
        let expectation = XCTestExpectation(description: #function)

        // WHEN fetchWalletPass is called
        sut.fetchWalletPass { result in
            // THEN
            XCTAssertEqual(result, .fetchFailed(outputError))

            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3.0)
    }

    func testWalletPassWhenPassNotCachedAndDataProviderSuccessAndMakeWalletFail() {
        // GIVEN stay
        let stay = getStay()

        // GIVEN mock pass manager and shouldThorwErrorForMakePass is true
        let walletPassManager = MockPassManager()
        walletPassManager.shouldThrowErrorForMakePass = true

        // GIVEN set up the sut
        sut = BookingConfirmationInteractor(
            summary: stay,
            isBookingFlowComplete: false,
            walletPassManager: walletPassManager
        )

        // GIVEN sut does not have a pass
        sut.pass = nil

        // GIVEN mock data provider with some data and no error
        let output = MockBookingConfirmationInteractorOutput()
        output.loadWalletPassResult = (Data(), nil)
        sut.dataProvider = output

        // GIVEN expectation
        let expectation = XCTestExpectation(description: #function)

        // WHEN fetchWalletPass is called
        sut.fetchWalletPass { result in
            // THEN
            XCTAssertEqual(result, .fetchFailed(NSError(domain: "SomeError", code: 202)))

            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3.0)
    }

    func testWalletPassWhenPassNotCachedAndDataProviderSuccessAndContainsPassTrue() {
        // GIVEN stay
        let stay = getStay()

        // GIVEN mock pass manager and shouldThorwErrorForMakePass is false
        let walletPassManager = MockPassManager()
        walletPassManager.shouldThrowErrorForMakePass = false
        walletPassManager.containsPassResult = true

        // GIVEN set up the sut
        sut = BookingConfirmationInteractor(
            summary: stay,
            isBookingFlowComplete: false,
            walletPassManager: walletPassManager
        )

        // GIVEN sut does not have a pass
        sut.pass = nil

        // GIVEN mock data provider with some data and no error
        let output = MockBookingConfirmationInteractorOutput()
        output.loadWalletPassResult = (Data(), nil)
        sut.dataProvider = output

        // GIVEN expectation
        let expectation = XCTestExpectation(description: #function)

        // WHEN fetchWalletPass is called
        sut.fetchWalletPass { result in
            // THEN
            XCTAssertEqual(result, .containsPass)

            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3.0)
    }

    func testWalletPassWhenPassNotCachedAndDataProviderSuccessAndContainsPassFalse() {
        // GIVEN stay
        let stay = getStay()

        // GIVEN mock pass manager and shouldThorwErrorForMakePass is false
        let walletPassManager = MockPassManager()
        walletPassManager.shouldThrowErrorForMakePass = false
        walletPassManager.containsPassResult = false

        // GIVEN set up the sut
        sut = BookingConfirmationInteractor(
            summary: stay,
            isBookingFlowComplete: false,
            walletPassManager: walletPassManager
        )

        // GIVEN sut does not have a pass
        sut.pass = nil

        // GIVEN mock data provider with some data and no error
        let output = MockBookingConfirmationInteractorOutput()
        output.loadWalletPassResult = (Data(), nil)
        sut.dataProvider = output

        // GIVEN expectation
        let expectation = XCTestExpectation(description: #function)

        // WHEN fetchWalletPass is called
        sut.fetchWalletPass { result in
            // THEN
            XCTAssertEqual(result, .fetchSuccessful(PKPass()))

            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 3.0)
    }
}
