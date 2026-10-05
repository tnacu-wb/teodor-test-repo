//
//  CiolReviewAndPayPresenterTests.swift
//  PremierInnTests
//
//  Created by Muresan, Andreea (Cognizant) on 02.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

final class CiolReviewAndPayPresenterTests: XCTestCase {
    var sut: CiolReviewAndPayPresenter!
    var mockView: MockCiolReviewAndPayView!
    var mockInteractor: MockCiolReviewAndPayInteractor!
    var router: CiolReviewAndPayRouter!
    
    override func setUp() {
        super.setUp()

        mockView = MockCiolReviewAndPayView()
        mockInteractor = MockCiolReviewAndPayInteractor()
        router = CiolReviewAndPayRouter(ciolFlow: .bookingConfirmation)
        
        sut = CiolReviewAndPayPresenter()
        sut.view = mockView
        sut.interactor = mockInteractor
        sut.router = router
    }
    
    override func tearDown() {
        sut = nil
        mockView = nil
        mockInteractor = nil
        router = nil
        super.tearDown()
    }
    
    func testHandlePayButtonTap_DisplaysActionLoadingIndicator() {
        // Act
        sut.handlePayButtonTap()
        
        // Assert
        XCTAssertTrue(mockView.isStartDisplayingActionLoadingElementsCalled)
    }

    func testUpdateSelectedPaymentMethod() {
        // Act
        sut.updateSelectedPaymentMethod(paymentViewModel: BookingDetails.BDPaymentMethodViewModel(type: .newBAC,
                                                                                                        imageUrls: nil,
                                                                                                        maskedPAN: "",
                                                                                                        cardholder: "",
                                                                                                        expiry: "",
                                                                                                        selected: true,
                                                                                                        cardName: "Visa Dragoi a",
                                                                                                        acceptedCardAccessibilityLabel: nil))
        
        // Assert
        XCTAssertTrue(mockInteractor.isSelectedPaymentMethodUpdated)
        XCTAssertTrue(mockView.isDataReloaded)
    }
    
    func testViewIsReady() {
        sut.viewIsReady()
        
        XCTAssertTrue(mockView.isLoadingIndicatorDisplayed)
        XCTAssertTrue(mockView.isPriceBreakdownLoaded)
        XCTAssertTrue(mockInteractor.isGetPaymentMethodsCalled)
    }
    
    func testHandlePayButton() {
        sut.handlePayButtonTap()
        
        XCTAssertTrue(mockView.isStartDisplayingActionLoadingElementsCalled)
    }

    func testUpdateStoredAddress() {
        sut.updateAddress(with: .postCode(""))

        XCTAssertTrue(mockInteractor.isUpdateStoredAddressCalled)
    }

    // MARK: - handlePayButtonTap

    func testHandlePayButtonTapWhenBackgroundChargeFalse() {
        // GIVEN
        mockInteractor.mockBackgroundChargeResult = false

        sut.interactor = mockInteractor

        // WHEN
        sut.handlePayButtonTap()

        // THEN
        let expectation = predicateExpectation(
            description: #function,
            self.mockView.isStartDisplayingActionLoadingElementsCalled == true &&
            self.mockView.isStopDisplayingActionLoadingElementsCalled == true &&
            self.mockInteractor.isTrackContinueButtonAnalyticsCalled == true &&
            self.mockView.isErrorDisplayed == true
        )

        wait(for: [expectation], timeout: 3)
    }

    func testHandlePayButtonTapWhenBackgroundChargeSuccess() {
        // GIVEN
        mockInteractor.mockBackgroundChargeResult = true

        sut.interactor = mockInteractor

        // WHEN
        sut.handlePayButtonTap()

        // THEN
        let expectation = predicateExpectation(
            description: #function,
            self.mockView.isStartDisplayingActionLoadingElementsCalled == true &&
            self.mockInteractor.isTrackContinueButtonAnalyticsCalled == true &&
            self.mockView.isErrorDisplayed == false
        )

        wait(for: [expectation], timeout: 3)
    }
}
