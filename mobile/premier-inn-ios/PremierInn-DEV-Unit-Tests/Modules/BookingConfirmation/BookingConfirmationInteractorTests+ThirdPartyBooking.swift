//
//  BookingConfirmationInteractorTests+ThirdPartyBooking.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 08/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import XCTest
import SimpleNetwork
@testable import PremierInn

extension BookingConfirmationInteractorTests {
    func testLoadLatestDetailsForThirdPartyBookingWhenCiolPaymentActionsError() {
        // GIVEN mock settings manager
        let settings = MockSettingsManager()
        settings.featureThirdPartyPrepaid = true

        // GIVEN sut
        sut = .init(
            summary: getStay(),
            isBookingFlowComplete: true,
            settingsManager: settings
        )

        // GIVEN mock presenter
        let presenter = MockBookingConfirmationPresenterInput()
        sut.presenter = presenter

        // GIVEN mock data provider
        let output = MockBookingConfirmationInteractorOutput()
        output.isThirdPartyBooking = true
        output.stubbedCiolPaymentActionsResponse = (nil, NSError(domain: "com.pi", code: 404))
        sut.dataProvider = output

        // THEN
        let expectationFetchHotel = predicateExpectation(
            description: "Fetch hotel Expectation",
            self.sut.hotel != nil &&
            presenter.isHotelFetchCalled
        )

        // WHEN prerequisite
        sut.fetchHotel()

        wait(for: [expectationFetchHotel], timeout: 3)

        let expectationLoadLatestDetails = XCTestExpectation(description: "Load latest details Expectation")

        // WHEN
        sut.loadLatestDetails { success in
            XCTAssertTrue(output.isCiolPaymentsActionsCalled)
            XCTAssertFalse(success)
            XCTAssertNil(self.sut.preStayInputParams?.ciolPaymentActions)
            expectationLoadLatestDetails.fulfill()
        }

        wait(for: [expectationLoadLatestDetails], timeout: 3)
    }

    func testLoadLatestDetailsForThirdPartyBookingWhenCiolPaymentActionsSuccess() {
        // GIVEN mock payment actions response
        let mockPaymentactionsResponse = CiolPaymentActionsResponse.cityTaxResponse

        // GIVEN mock settings manager
        let settings = MockSettingsManager()
        settings.featureThirdPartyPrepaid = true

        // GIVEN sut
        sut = .init(
            summary: getStay(),
            isBookingFlowComplete: true,
            settingsManager: settings
        )

        // GIVEN mock presenter
        let presenter = MockBookingConfirmationPresenterInput()
        sut.presenter = presenter

        // GIVEN mock data provider
        let output = MockBookingConfirmationInteractorOutput()
        output.isThirdPartyBooking = true
        output.stubbedCiolPaymentActionsResponse = (mockPaymentactionsResponse, nil)
        sut.dataProvider = output

        // THEN
        let expectationFetchHotel = predicateExpectation(
            description: "Fetch hotel Expectation",
            self.sut.hotel != nil &&
            presenter.isHotelFetchCalled
        )

        // WHEN prerequisite
        sut.fetchHotel()

        wait(for: [expectationFetchHotel], timeout: 3)

        let expectationLoadLatestDetails = XCTestExpectation(description: "Load latest details Expectation")

        // WHEN
        sut.loadLatestDetails { success in
            XCTAssertTrue(output.isCiolPaymentsActionsCalled)
            XCTAssertTrue(success)
            XCTAssertEqual(
                self.sut.preStayInputParams?.ciolPaymentActions?.cityTax,
                mockPaymentactionsResponse.cityTax
            )
            expectationLoadLatestDetails.fulfill()
        }

        wait(for: [expectationLoadLatestDetails], timeout: 3)
    }
}
